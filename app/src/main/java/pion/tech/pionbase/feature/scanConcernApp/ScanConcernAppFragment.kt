package pion.tech.pionbase.feature.scanConcernApp

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentScanConcernAppBinding
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class ScanConcernAppFragment :
    BaseFragment<FragmentScanConcernAppBinding, ScanConcernAppViewModel, CommonViewModel>(
        FragmentScanConcernAppBinding::inflate,
        ScanConcernAppViewModel::class.java,
        CommonViewModel::class.java,
    ) {

    override fun init(view: View) {
        startScanEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.concernAppsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    showHideLoading(true)
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnStartScan.isEnabled = false
                    logger.logEvent("concern_apps_scanning")
                },
                onSuccess = { concernApps ->
                    showHideLoading(false)
                    binding.progressBar.visibility = View.GONE
                    binding.btnStartScan.isEnabled = true
                    logger.logEvent("concern_apps_scan_completed") {
                        putString("count", concernApps.size.toString())
                    }
                    // Navigate to ResultConcernApp screen with the results
                    navigateToResultScreen(concernApps)
                },
                onError = { exception ->
                    showHideLoading(false)
                    binding.progressBar.visibility = View.GONE
                    binding.btnStartScan.isEnabled = true
                    logger.logEvent("concern_apps_scan_error") {
                        putString("error_message", exception.message ?: "Unknown error")
                    }
                    // Handle error - could show a toast or dialog
                },
            )
        }
    }
}