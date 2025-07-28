package pion.tech.pionbase.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import pion.tech.pionbase.data.local.dao.PopupDetectionDAO
import pion.tech.pionbase.service.popupdetection.*
import javax.inject.Inject

/**
 * Simplified PopupDetectionService without AdNodeAnalyzer
 * Uses only pattern detection for better performance and stability
 */
@AndroidEntryPoint
class PopupDetectionService : AccessibilityService() {
    companion object {
        private const val TAG = "PopupDetectionService"

        // Event processing limits to prevent system overload
        private const val MAX_EVENTS_PER_SECOND = 15
        private const val MEMORY_CLEANUP_INTERVAL_MS = 30000L // 30 seconds
    }

    @Inject
    lateinit var popupDetectionDAO: PopupDetectionDAO

    private lateinit var detectionManager: PopupDetectionManager
    private lateinit var eventHandlers: List<AccessibilityEventHandler>

    // Service lifecycle management
    private var serviceScope: CoroutineScope? = null
    private var isServiceActive = false

    // Event throttling
    private var eventCount = 0
    private var lastEventResetTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()

        try {
            Log.d(TAG, "Popup Detection Service Connected")
            isServiceActive = true

            // Initialize coroutine scope with error handler
            serviceScope =
                CoroutineScope(
                    Dispatchers.Main + SupervisorJob() +
                        CoroutineExceptionHandler { _, throwable ->
                            Log.e(TAG, "Uncaught exception in service scope", throwable)
                        },
                )

            // Initialize components with error handling
            initializeComponents()

            // Configure accessibility service
            configureAccessibilityService()

            // Start memory cleanup task
            startMemoryCleanupTask()
        } catch (e: Exception) {
            Log.e(TAG, "Error during service connection", e)
            handleServiceError(e)
        }
    }

    private fun initializeComponents() {
        try {
            detectionManager = PopupDetectionManager(this, popupDetectionDAO)
            eventHandlers = EventHandlerFactory.createHandlers()
            Log.d(TAG, "Components initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing components", e)
            throw e
        }
    }

    private fun configureAccessibilityService() {
        try {
            val info =
                AccessibilityServiceInfo().apply {
                    eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                        AccessibilityEvent.TYPE_VIEW_CLICKED or
                        AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED

                    feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                    flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                        AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS

                    notificationTimeout = 100 // Reduced complexity for better performance
                }
            serviceInfo = info
            Log.d(TAG, "Accessibility service configured")
        } catch (e: Exception) {
            Log.e(TAG, "Error configuring accessibility service", e)
            throw e
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isServiceActive || event == null) return

        try {
            // Throttle events to prevent system overload
            if (!shouldProcessEvent()) {
                return
            }

            // Process event safely
            processEventSafely(event)
        } catch (e: Exception) {
            Log.w(TAG, "Error processing accessibility event", e)
            // Don't crash the service, just log and continue
        }
    }

    private fun shouldProcessEvent(): Boolean {
        val currentTime = System.currentTimeMillis()

        // Reset counter every second
        if (currentTime - lastEventResetTime > 1000) {
            eventCount = 0
            lastEventResetTime = currentTime
        }

        // Check if we're exceeding the rate limit
        if (eventCount >= MAX_EVENTS_PER_SECOND) {
            Log.d(TAG, "Event rate limiting applied")
            return false
        }

        eventCount++
        return true
    }

    private fun processEventSafely(event: AccessibilityEvent) {
        try {
            // Find appropriate handler and delegate
            val handler = eventHandlers.find { it.canHandle(event.eventType) }

            if (handler != null) {
                // Process in background to avoid blocking main thread
                serviceScope?.launch(Dispatchers.IO) {
                    try {
                        handler.handle(event, this@PopupDetectionService, detectionManager)
                    } catch (e: Exception) {
                        Log.w(TAG, "Error in event handler", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error in processEventSafely", e)
        }
    }

    private fun startMemoryCleanupTask() {
        serviceScope?.launch {
            while (isServiceActive) {
                try {
                    delay(MEMORY_CLEANUP_INTERVAL_MS)
                    performMemoryCleanup()
                } catch (e: Exception) {
                    Log.w(TAG, "Error during memory cleanup", e)
                }
            }
        }
    }

    private fun performMemoryCleanup() {
        try {
            // Suggest garbage collection
            System.gc()
            Log.d(TAG, "Memory cleanup performed")
        } catch (e: Exception) {
            Log.w(TAG, "Error during memory cleanup", e)
        }
    }

    private fun handleServiceError(error: Exception) {
        Log.e(TAG, "Service error occurred", error)

        try {
            // Attempt to reinitialize components
            if (::popupDetectionDAO.isInitialized) {
                initializeComponents()
                Log.i(TAG, "Service components reinitialized after error")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to recover from service error", e)
            // Don't crash, let system handle the service restart
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Popup Detection Service Interrupted")
        isServiceActive = false

        try {
            serviceScope?.cancel()
        } catch (e: Exception) {
            Log.w(TAG, "Error canceling service scope", e)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Popup Detection Service Destroyed")
        isServiceActive = false

        try {
            // Cancel all coroutines
            serviceScope?.cancel()

            Log.d(TAG, "Service cleanup completed")
        } catch (e: Exception) {
            Log.w(TAG, "Error during service destruction", e)
        } finally {
            super.onDestroy()
        }
    }
}
