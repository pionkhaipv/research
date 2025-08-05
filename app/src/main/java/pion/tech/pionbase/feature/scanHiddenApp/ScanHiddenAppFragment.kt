package pion.tech.pionbase.feature.scanHiddenApp

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.piontech.core.base.BaseFragment
import com.piontech.core.base.launchIO
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pion.tech.pionbase.R
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentScanHiddenAppBinding
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class ScanHiddenAppFragment :
    BaseFragment<FragmentScanHiddenAppBinding, ScanHiddenAppViewModel, CommonViewModel>(
        FragmentScanHiddenAppBinding::inflate,
        ScanHiddenAppViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    override fun init(view: View) {
        initView()
        startScanEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.scanTimeSeconds.collectFlowOnView(viewLifecycleOwner) { seconds ->
            binding.tvTimeScanning.text =
                buildString {
                    append(seconds)
                    append("s")
                }
        }

        viewModel.hiddenAppsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onNone = {
                    binding.clStartScan.isInvisible = false
                    binding.clScanning.isInvisible = true
                    binding.lottieScanning.pauseAnimation()
                },
                onLoading = {
                    binding.clStartScan.isInvisible = true
                    binding.clScanning.isInvisible = false
                    binding.lottieScanning.playAnimation()
                },
                onSuccess = { hiddenApps ->
                    launchIO {
                        delay(3000)
                        withContext(Dispatchers.Main) {
                            binding.lottieScanning.pauseAnimation()
                            navigateToResultScreen(hiddenApps)
                            viewModel.resetScanState()
                        }
                    }
                },
                onError = { exception ->
                    binding.clStartScan.isInvisible = false
                    binding.clScanning.isInvisible = true
                    binding.lottieScanning.pauseAnimation()
                    displayToast(R.string.something_error)
                },
            )
        }
    }
}
