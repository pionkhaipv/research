package pion.tech.pionbase.feature.resultHiddenApp

import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.core.view.isVisible
import com.piontech.core.utils.parcelableArrayList
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.feature.resultHiddenApp.adapter.HiddenAppsAdapter
import pion.tech.pionbase.util.BundleKey
import pion.tech.pionbase.util.setPreventDoubleClick

fun ResultHiddenAppFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
    binding.btnBack.setPreventDoubleClick {
        backEvent()
    }
}

fun ResultHiddenAppFragment.initView() {
    val hiddenApps =
        arguments?.parcelableArrayList<HiddenAppUIModel>(BundleKey.KEY_HIDDEN_APP_RESULT)
            ?: emptyList()
    viewModel.setHiddenApps(hiddenApps)

    adapter.setListener(this)
    binding.rvHiddenApps.adapter = adapter
}

fun ResultHiddenAppFragment.backEvent() {
    navigator.navigateUp()
}

// This function is no longer needed as we're using viewModel.groupedApps directly in subscribeObserver

fun ResultHiddenAppFragment.openAppSettings(hiddenApp: HiddenAppUIModel) {
    try {
        // Open the specific app's settings screen
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", hiddenApp.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        startActivity(intent)
    } catch (e: Exception) {
        // Fallback to general settings if something goes wrong
        try {
            val intent =
                Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            startActivity(intent)
        } catch (fallbackException: Exception) {
            // Log error or show toast if needed
        }
    }
}
