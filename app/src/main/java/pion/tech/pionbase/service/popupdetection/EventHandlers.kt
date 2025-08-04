package pion.tech.pionbase.service.popupdetection

import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * Handler for different types of accessibility events
 * Simplified without AdNodeAnalyzer - relies on pattern detection only
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

        // Check each pattern type for better debugging
        val isLikelyPopup = AdPatternDetector.isLikelyPopup(className)
        val isKnownAdActivity = AdPatternDetector.isKnownAdActivity(className)
        val isKnownAdPackage = AdPatternDetector.isKnownAdPackage(packageName)

        Log.d(TAG, "Pattern check - isLikelyPopup: $isLikelyPopup, isKnownAdActivity: $isKnownAdActivity, isKnownAdPackage: $isKnownAdPackage")

        // Direct pattern detection without node analysis
        if (isLikelyPopup || isKnownAdActivity || isKnownAdPackage) {
            Log.i(TAG, "Detected potential ad/popup: $packageName, $className")
            val appName = manager.getAppName(packageName)
            manager.reportPopupDetected(packageName, appName, "WINDOW_AD")
        } else {
            Log.v(TAG, "No popup pattern matched for: $packageName, $className")
        }
    }
}

class WindowContentChangedHandler : AccessibilityEventHandler {
    companion object {
        private const val TAG = "WindowContentChangedHandler"
    }

    override fun canHandle(eventType: Int): Boolean = eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED

    override fun handle(
        event: AccessibilityEvent,
        service: PopupDetectionService,
        manager: PopupDetectionManager,
    ) {
        val packageName = event.packageName?.toString() ?: return

        // Skip system packages and own package
        if (packageName == service.packageName || AdPatternDetector.isSystemPackage(packageName)) {
            Log.v(TAG, "Skipping system/own package: $packageName")
            return
        }

        val className = event.className?.toString()
        Log.d(TAG, "Window content changed: $packageName, $className")

        // Direct pattern check without node analysis
        if (className != null) {
            val isLikelyPopup = AdPatternDetector.isLikelyPopup(className)
            val isLikelyAdRelated = AdPatternDetector.isLikelyAdRelated(className)
            val isKnownAdPackage = AdPatternDetector.isKnownAdPackage(packageName)

            Log.d(TAG, "Pattern check - isLikelyPopup: $isLikelyPopup, isLikelyAdRelated: $isLikelyAdRelated, isKnownAdPackage: $isKnownAdPackage")

            if (isLikelyPopup || isLikelyAdRelated || isKnownAdPackage) {
                Log.i(TAG, "Detected content ad/popup: $packageName, $className")
                // Report directly without delayed analysis
                val appName = manager.getAppName(packageName)
                manager.reportPopupDetected(packageName, appName, "CONTENT_AD")
            } else {
                Log.v(TAG, "No content popup pattern matched for: $packageName, $className")
            }
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

        // Simple pattern check on package name only
        if (AdPatternDetector.isKnownAdPackage(packageName)) {
            val appName = manager.getAppName(packageName)
            manager.reportPopupDetected(packageName, appName, "AD_CLICK")
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
