package pion.tech.pionbase.feature.scanHiddenApp

import android.os.Bundle
import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.hiddenApp.HiddenAppUIModel
import pion.tech.pionbase.util.BundleKey
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun ScanHiddenAppFragment.startScanEvent() {
    binding.btnStartScan.setPreventDoubleClickScaleView {
        viewModel.startScan()
    }
}

fun ScanHiddenAppFragment.initView() {
    binding.lottieScanning.pauseAnimation()
}

fun ScanHiddenAppFragment.onBackEvent() {
    binding.btnBack.setPreventDoubleClick {
        backEvent()
    }
    onSystemBack {
        backEvent()
    }
}

fun ScanHiddenAppFragment.backEvent() {
    navigator.navigateUp()
}

fun ScanHiddenAppFragment.navigateToResultScreen(hiddenApps: List<HiddenAppUIModel>) {
    val bundle =
        Bundle().apply {
            putParcelableArrayList(BundleKey.KEY_HIDDEN_APP_RESULT, ArrayList(hiddenApps))
        }
    navigator.navigateTo(R.id.action_scanHiddenAppFragment_to_resultHiddenAppFragment, bundle)
}
