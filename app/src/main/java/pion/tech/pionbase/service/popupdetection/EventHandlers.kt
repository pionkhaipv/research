package pion.tech.pionbase.service.popupdetection

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import pion.tech.pionbase.service.PopupDetectionService

/**
 * Handler for different types of accessibility events
 * Implements Strategy pattern for event handling
 */
interface AccessibilityEventHandler {
    fun canHandle(eventType: Int): Boolean

    fun handle(
        event: AccessibilityEvent,
        service: PopupDetectionService,
        manager: PopupDetectionManager,
    )
}

class WindowStateChangedHandler : AccessibilityEventHandler {
    companion object {
        private const val TAG = "WindowStateChangedHandler"
    }

    override fun canHandle(eventType: Int): Boolean = eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED

    override fun handle(
        event: AccessibilityEvent,
        service: PopupDetectionService,
        manager: PopupDetectionManager,
    ) {
        val packageName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: return

        Log.d(TAG, "Window state changed: $packageName, $className")

        // Check if it's likely a popup/ad
        if (AdPatternDetector.isLikelyPopup(className) ||
            AdPatternDetector.isKnownAdActivity(className) ||
            AdPatternDetector.isKnownAdPackage(packageName)
        ) {
            Log.d(TAG, "Detected potential ad/popup - analyzing: $packageName, $className")
            service.analyzeCurrentWindow(packageName)
        }
    }
}

class WindowContentChangedHandler : AccessibilityEventHandler {
    override fun canHandle(eventType: Int): Boolean = eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED

    override fun handle(
        event: AccessibilityEvent,
        service: PopupDetectionService,
        manager: PopupDetectionManager,
    ) {
        val packageName = event.packageName?.toString() ?: return

        // Skip system packages and own package
        if (packageName == service.packageName || AdPatternDetector.isSystemPackage(packageName)) return

        val className = event.className?.toString()
        if (className != null &&
            !AdPatternDetector.isLikelyPopup(className) &&
            !AdPatternDetector.isLikelyAdRelated(className)
        ) {
            return
        }

        // Schedule delayed analysis to let UI stabilize
        manager.scheduleDelayedAnalysis(packageName) {
            service.analyzeCurrentWindow(packageName)
        }
    }
}

class ViewClickedHandler : AccessibilityEventHandler {
    companion object {
        private const val TAG = "ViewClickedHandler"
    }

    override fun canHandle(eventType: Int): Boolean = eventType == AccessibilityEvent.TYPE_VIEW_CLICKED

    override fun handle(
        event: AccessibilityEvent,
        service: PopupDetectionService,
        manager: PopupDetectionManager,
    ) {
        val packageName = event.packageName?.toString() ?: return

        // Skip own package
        if (packageName == service.packageName) return

        event.source?.let { nodeInfo ->
            try {
                val analyzer = manager.getAdNodeAnalyzer()
                if (analyzer.isLikelyAdView(nodeInfo)) {
                    val result = analyzer.analyzeForAds(nodeInfo)
                    if (result.isAd) {
                        val appName = manager.getAppName(packageName)
                        manager.reportPopupDetected(packageName, appName, "AD_CLICK")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error analyzing clicked view", e)
            }
        }
    }
}

class NotificationChangedHandler : AccessibilityEventHandler {
    override fun canHandle(eventType: Int): Boolean = eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED

    override fun handle(
        event: AccessibilityEvent,
        service: PopupDetectionService,
        manager: PopupDetectionManager,
    ) {
        val textList = event.text ?: return
        val text = textList.joinToString(" ")
        val packageName = event.packageName?.toString() ?: return

        if (AdPatternDetector.containsAdKeywords(text)) {
            val appName = manager.getAppName(packageName)
            manager.reportPopupDetected(packageName, appName, "NOTIFICATION_AD")
        }
    }
}

/**
 * Factory for creating event handlers
 */
object EventHandlerFactory {
    fun createHandlers(): List<AccessibilityEventHandler> =
        listOf(
            WindowStateChangedHandler(),
            WindowContentChangedHandler(),
            ViewClickedHandler(),
            NotificationChangedHandler(),
        )
}
