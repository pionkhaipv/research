package pion.tech.pionbase.feature.home

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.base.doActionWhenResume
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.DemoMultipleAdapter
import pion.tech.pionbase.feature.home.dialog.DemoDialog
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel, CommonViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class.java,
        CommonViewModel::class.java,
    ),
    DemoDialog.Listener {
    val adapter = DemoMultipleAdapter()

    override fun init(view: View) {
        initView()
        plusEvent()
        settingEvent()
        notificationManagerEvent()
        runningAppsEvent()
        checkHiddenAppsEvent()
        showConcernEvent()
        onBackEvent()
        adDetectorEvent()

        // Load installed apps
        viewModel.getInstalledApps()

        // Check permissions when fragment resumes (e.g., returning from settings)
        doActionWhenResume {
            checkPermissionsOnResume()
        }
    }

    override fun subscribeObserver(view: View) {
        viewModel.countValue.collectFlowOnView(viewLifecycleOwner) {
//            binding.tvCount.text = "$it"
        }

        // Observe installed apps state
        viewModel.installedAppsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    showHideLoading(true)
                    logger.logEvent("installed_apps_loading")
                },
                onSuccess = { installedApps ->
                    showHideLoading(false)
                    logger.logEvent("installed_apps_loaded") {
                        putString("count", installedApps.size.toString())
                    }
                    // Handle the list of installed apps here
                    // You can update UI, show in RecyclerView, etc.
                },
                onError = { exception ->
                    showHideLoading(false)
                    logger.logEvent("installed_apps_error") {
                        putString("error_message", exception.message ?: "Unknown error")
                    }
                    displayToast("Failed to load installed apps: ${exception.message}")
                },
            )
        }
    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }
}
