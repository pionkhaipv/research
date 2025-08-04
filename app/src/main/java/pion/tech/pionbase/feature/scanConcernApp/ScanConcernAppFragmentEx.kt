package pion.tech.pionbase.feature.scanConcernApp

import android.os.Bundle
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.concernApp.ConcernAppUIModel
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun ScanConcernAppFragment.startScanEvent() {
    binding.btnStartScan.setPreventDoubleClickScaleView {
        // Check permissions first (for now, we'll skip permission checking)
        // In a real implementation, you might want to check for specific permissions
        // For scanning apps, usually no special permissions are needed beyond what's already granted

        // Start the scan
        viewModel.startScan()
    }
}

fun ScanConcernAppFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun ScanConcernAppFragment.backEvent() {
    navigator.navigateUp()
}

fun ScanConcernAppFragment.navigateToResultScreen(concernApps: List<ConcernAppUIModel>) {
    val bundle =
        Bundle().apply {
            putParcelableArrayList("concernApps", ArrayList(concernApps))
        }
    navigator.navigateTo(R.id.action_scanConcernAppFragment_to_resultConcernAppFragment, bundle)
}
