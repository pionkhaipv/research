package pion.tech.pionbase.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.data.roomDb.dao.PopupDetectionDAO
import pion.tech.pionbase.service.popupdetection.*
import javax.inject.Inject

/**
 * Optimized PopupDetectionService using Strategy and Observer patterns
 * Delegated complex logic to specialized classes for better maintainability
 */
@AndroidEntryPoint
class PopupDetectionService : AccessibilityService() {
    companion object {
        private const val TAG = "PopupDetectionService"
    }

    @Inject
    lateinit var popupDetectionDAO: PopupDetectionDAO

    private lateinit var detectionManager: PopupDetectionManager
    private lateinit var eventHandlers: List<AccessibilityEventHandler>

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Popup Detection Service Connected")

        // Initialize components
        detectionManager = PopupDetectionManager(this, popupDetectionDAO)
        eventHandlers = EventHandlerFactory.createHandlers()

        // Configure accessibility service
        configureAccessibilityService()
    }

    private fun configureAccessibilityService() {
        val info =
            AccessibilityServiceInfo().apply {
                eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOWS_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_FOCUSED or
                    AccessibilityEvent.TYPE_VIEW_SCROLLED

                feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY or
                    AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE

                notificationTimeout = 50
            }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { accessibilityEvent ->
            // Find appropriate handler and delegate
            eventHandlers
                .find { it.canHandle(accessibilityEvent.eventType) }
                ?.handle(accessibilityEvent, this, detectionManager)
        }
    }

    fun analyzeCurrentWindow(packageName: String) {
        if (detectionManager.shouldThrottleAnalysis(packageName)) return

        try {
            val rootNode = rootInActiveWindow ?: return
            val analysisResult = detectionManager.getAdNodeAnalyzer().analyzeForAds(rootNode)

            if (analysisResult.isAd) {
                val appName = detectionManager.getAppName(packageName)
                Log.d(TAG, "Popup ad detected in $appName ($packageName)")

                detectionManager.reportPopupDetected(
                    packageName = packageName,
                    appName = appName,
                    popupType = analysisResult.adType,
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing current window", e)
        } finally {
            detectionManager.updateAnalysisTime(packageName)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Popup Detection Service Interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Popup Detection Service Destroyed")
    }
}
