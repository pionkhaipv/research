package pion.tech.pionbase.feature.scanHiddenApp

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentScanHiddenAppBinding
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class ScanHiddenAppFragment :
    BaseFragment<FragmentScanHiddenAppBinding, ScanHiddenAppViewModel, CommonViewModel>(
        FragmentScanHiddenAppBinding::inflate,
        ScanHiddenAppViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    override fun init(view: View) {
        startScanEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.hiddenAppsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    showHideLoading(true)
                    binding.btnStartScan.isEnabled = false
                    logger.logEvent("hidden_apps_scanning")
                },
                onSuccess = { hiddenApps ->
                    showHideLoading(false)
                    binding.btnStartScan.isEnabled = true
                    logger.logEvent("hidden_apps_scan_completed") {
                        putString("count", hiddenApps.size.toString())
                    }
                    // Navigate to ResultHiddenApp screen with the results
                    navigateToResultScreen(hiddenApps)
                },
                onError = { exception ->
                    showHideLoading(false)
                    binding.btnStartScan.isEnabled = true
                    logger.logEvent("hidden_apps_scan_error") {
                        putString("error_message", exception.message ?: "Unknown error")
                    }
                    // Handle error - could show a toast or dialog
                },
            )
        }
    }
}
