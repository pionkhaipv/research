package pion.tech.pionbase.feature.resultHiddenApp

import android.content.Intent
import android.provider.Settings
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.feature.resultHiddenApp.adapter.HiddenAppsAdapter
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun ResultHiddenAppFragment.scanAgainEvent() {
    binding.btnScanAgain.setPreventDoubleClickScaleView {
        // Navigate back to ScanHiddenApp screen
        navigator.navigateUp()
    }
}

fun ResultHiddenAppFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun ResultHiddenAppFragment.backEvent() {
    // Simply go back to the previous screen
    navigator.navigateUp()
}

fun ResultHiddenAppFragment.setupAdapterClickListeners(adapter: HiddenAppsAdapter) {
    adapter.onAppClick = { hiddenApp ->
        openAppSettings(hiddenApp)
    }
}

fun ResultHiddenAppFragment.openAppSettings(hiddenApp: HiddenAppUIModel) {
    try {
        // Open the specific app's settings screen
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", hiddenApp.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    } catch (e: Exception) {
        // Fallback to general settings if something goes wrong
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (fallbackException: Exception) {
            // Log error or show toast if needed
        }
    }
}
