package pion.tech.pionbase.service.popupdetection

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import pion.tech.pionbase.data.local.dao.PopupDetectionDAO
import javax.inject.Inject

/**
 * Simple popup detection service using accessibility events
 */
@AndroidEntryPoint
class PopupDetectionService : AccessibilityService() {

    @Inject
    lateinit var popupDetectionDAO: PopupDetectionDAO

    private lateinit var detectionManager: PopupDetectionManager
    private lateinit var eventHandlers: List<AccessibilityEventHandler>
    private var serviceScope: CoroutineScope? = null
    private var isActive = false

    override fun onServiceConnected() {
        super.onServiceConnected()

        Log.d("PopupService", "Service connected")
        isActive = true

        // Setup coroutine scope
        serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        // Initialize components
        detectionManager = PopupDetectionManager(this, popupDetectionDAO)
        eventHandlers = EventHandlerFactory.createHandlers()

        // Configure service
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                        AccessibilityEvent.TYPE_VIEW_CLICKED or
                        AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                   AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isActive || event == null) return

        // Find handler for this event type
        val handler = eventHandlers.find { it.canHandle(event.eventType) }

        handler?.let {
            // Process event in background
            serviceScope?.launch {
                try {
                    it.handle(event, this@PopupDetectionService, detectionManager)
                } catch (e: Exception) {
                    Log.w("PopupService", "Error handling event", e)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("PopupService", "Service interrupted")
        cleanup()
    }

    override fun onDestroy() {
        Log.d("PopupService", "Service destroyed")
        cleanup()
        super.onDestroy()
    }

    private fun cleanup() {
        isActive = false
        serviceScope?.cancel()
    }
}
