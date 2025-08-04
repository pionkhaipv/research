package pion.tech.pionbase.feature.scanHiddenApp

import android.os.Bundle
import android.util.Log
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun ScanHiddenAppFragment.startScanEvent() {
    binding.btnStartScan.setPreventDoubleClickScaleView {
        // Check permissions first (for now, we'll skip permission checking)
        // In a real implementation, you might want to check for specific permissions
        // For scanning apps, usually no special permissions are needed beyond what's already granted

        // Start the scan
        viewModel.startScan()
    }
}

fun ScanHiddenAppFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun ScanHiddenAppFragment.backEvent() {
    // Reset scan state when going back
    viewModel.resetScanState()
}

fun ScanHiddenAppFragment.navigateToResultScreen(hiddenApps: List<HiddenAppUIModel>) {
    val bundle =
        Bundle().apply {
            putParcelableArrayList("hiddenApps", ArrayList(hiddenApps))
        }
    navigator.navigateTo(R.id.action_scanHiddenAppFragment_to_resultHiddenAppFragment, bundle)
}
