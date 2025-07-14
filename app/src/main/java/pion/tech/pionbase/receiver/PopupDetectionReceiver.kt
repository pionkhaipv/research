package pion.tech.pionbase.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.service.PopupDetectionService

@AndroidEntryPoint
class PopupDetectionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "PopupDetectionReceiver"
    }

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (intent?.action == PopupDetectionService.ACTION_POPUP_DETECTED) {
            val appPackage = intent.getStringExtra(PopupDetectionService.EXTRA_APP_PACKAGE)
            val appName = intent.getStringExtra(PopupDetectionService.EXTRA_APP_NAME)
            val popupType = intent.getStringExtra(PopupDetectionService.EXTRA_POPUP_TYPE)
            val detectionTime = intent.getLongExtra(PopupDetectionService.EXTRA_DETECTION_TIME, 0L)

            Log.i(TAG, "Popup detected: $appName ($appPackage) - Type: $popupType")

            // Xử lý khi phát hiện popup quảng cáo
            handlePopupDetected(context, appPackage, appName, popupType, detectionTime)
        }
    }

    private fun handlePopupDetected(
        context: Context?,
        appPackage: String?,
        appName: String?,
        popupType: String?,
        detectionTime: Long,
    ) {
        context ?: return
        appPackage ?: return
        appName ?: return

        // Có thể thêm logic để:
        // 1. Lưu vào database
        // 2. Hiển thị notification
        // 3. Gửi analytics
        // 4. Cập nhật UI nếu app đang mở

        Log.d(TAG, "Processing popup detection for $appName")
    }
}
