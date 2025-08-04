package pion.tech.pionbase.service.popupdetection

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pion.tech.pionbase.data.local.dao.PopupDetectionDAO
import pion.tech.pionbase.data.model.popupDetection.PopupDetectionEntity

/**
 * Manager class for handling popup detection logic and reporting
 * Simplified without AdNodeAnalyzer dependency
 */
class PopupDetectionManager(
    private val context: Context,
    private val popupDetectionDAO: PopupDetectionDAO,
) {
    companion object {
        private const val TAG = "PopupDetectionManager"

        // Action constants
        const val ACTION_POPUP_DETECTED = "pion.tech.pionbase.POPUP_DETECTED"
        const val EXTRA_APP_PACKAGE = "app_package"
        const val EXTRA_APP_NAME = "app_name"
        const val EXTRA_POPUP_TYPE = "popup_type"
        const val EXTRA_DETECTION_TIME = "detection_time"

        // Throttling constants - reduced for better detection
        const val ANALYSIS_THROTTLE_MS = 300L
        const val MIN_TIME_BETWEEN_SAME_PACKAGE_MS = 500L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val packageManager by lazy { context.packageManager }
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    // Throttling state
    private var lastAnalysisTime = 0L
    private var lastAnalyzedPackage = ""
    private var lastAnalyzedPackageTime = 0L

    fun shouldThrottleAnalysis(packageName: String): Boolean {
        val currentTime = System.currentTimeMillis()

        // Check global throttling
        if (currentTime - lastAnalysisTime < ANALYSIS_THROTTLE_MS) {
            Log.d(TAG, "Throttling analysis for package: $packageName")
            return true
        }

        // Check package-specific throttling
        if (packageName == lastAnalyzedPackage &&
            currentTime - lastAnalyzedPackageTime < MIN_TIME_BETWEEN_SAME_PACKAGE_MS
        ) {
            Log.d(TAG, "Skipping analysis for the same package: $packageName")
            return true
        }

        return false
    }

    fun updateAnalysisTime(packageName: String) {
        val currentTime = System.currentTimeMillis()
        lastAnalysisTime = currentTime
        lastAnalyzedPackage = packageName
        lastAnalyzedPackageTime = currentTime
    }

    fun getAppName(packageName: String): String =
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }

    fun reportPopupDetected(
        packageName: String,
        appName: String,
        popupType: String,
    ) {
        Log.i(TAG, "Popup detected - App: $appName, Package: $packageName, Type: $popupType")

        val currentTime = System.currentTimeMillis()

        // Save to database
        saveToDatabase(packageName, appName, popupType, currentTime)

        // Show notification
        PopupNotificationHelper.showPopupDetectedNotification(
            context = context,
            appName = appName,
            popupType = popupType,
        )

        // Send broadcast
        sendBroadcast(packageName, appName, popupType, currentTime)
    }

    private fun saveToDatabase(
        packageName: String,
        appName: String,
        popupType: String,
        time: Long,
    ) {
        serviceScope.launch {
            try {
                val popupDetection =
                    PopupDetectionEntity(
                        appPackage = packageName,
                        appName = appName,
                        popupType = popupType,
                        detectionTime = time,
                    )

                val insertedId = popupDetectionDAO.insert(popupDetection)
                Log.d(TAG, "Popup detection saved to database with ID: $insertedId")

                // Clean up old records (30 days)
                val thirtyDaysAgo = time - (30 * 24 * 60 * 60 * 1000L)
                val deletedCount = popupDetectionDAO.deleteOldRecords(thirtyDaysAgo)
                if (deletedCount > 0) {
                    Log.d(TAG, "Deleted $deletedCount old popup detection records")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving popup detection to database", e)
            }
        }
    }

    private fun sendBroadcast(
        packageName: String,
        appName: String,
        popupType: String,
        time: Long,
    ) {
        val intent =
            Intent(ACTION_POPUP_DETECTED).apply {
                putExtra(EXTRA_APP_PACKAGE, packageName)
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_POPUP_TYPE, popupType)
                putExtra(EXTRA_DETECTION_TIME, time)
            }

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun scheduleDelayedAnalysis(
        packageName: String,
        delay: Long = 500L,
        analysisAction: () -> Unit,
    ) {
        handler.removeCallbacksAndMessages(packageName)
        handler.postDelayed({
            analysisAction()
        }, delay)
    }
}
