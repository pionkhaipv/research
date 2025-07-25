package pion.tech.pionbase.feature.notificationManager.notificationPermissions

import android.view.View
import androidx.core.view.isVisible
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentNotificationPermissionsBinding
import pion.tech.pionbase.feature.notificationManager.adapter.AppNotificationPermissionAdapter
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState
import timber.log.Timber

@AndroidEntryPoint
class NotificationPermissionsFragment :
    BaseFragment<FragmentNotificationPermissionsBinding, NotificationPermissionsViewModel, CommonViewModel>(
        FragmentNotificationPermissionsBinding::inflate,
        NotificationPermissionsViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    lateinit var appPermissionsAdapter: AppNotificationPermissionAdapter

    override fun init(view: View) {
        logger.logScreen("notification_permissions_show")
        initView()
        loadData()
    }

    override fun subscribeObserver(view: View) {
        viewModel.appPermissionsUiState.collectFlowOnView(viewLifecycleOwner) {
            val tag = "appPermissionsUiState"
            Timber.tag(tag).d(it.toString())
            it.handleUiState(
                onLoading = {
                    // Show loading state
                    binding.rvAppPermissions.isVisible = false
                    binding.tvEmptyState.isVisible = false
                },
                onSuccess = { apps ->
                    // Handle the list of apps with notification permissions
                    if (apps.isEmpty()) {
                        binding.rvAppPermissions.isVisible = false
                        binding.tvEmptyState.isVisible = true
                        binding.tvEmptyState.text = "No apps found with notification permissions"
                    } else {
                        binding.rvAppPermissions.isVisible = true
                        binding.tvEmptyState.isVisible = false
                        appPermissionsAdapter.submitList(apps)
                    }
                },
                onError = { exception ->
                    binding.rvAppPermissions.isVisible = false
                    binding.tvEmptyState.isVisible = true
                    binding.tvEmptyState.text = "Failed to load app permissions: ${exception.message}"
                    displayToast("Failed to load app permissions: ${exception.message}")
                    logger.logEvent("app_permissions_error") {
                        putString("error_message", exception.message ?: "Unknown error")
                    }
                },
            )
        }

        viewModel.togglePermissionUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    // Handle loading state for toggle operations
                },
                onSuccess = { success ->
                    if (success) {
                        displayToast("Permission updated successfully")
                    }
                },
                onError = { exception ->
                    displayToast("Failed to update permission: ${exception.message}")
                    logger.logEvent("toggle_permission_error") {
                        putString("error_message", exception.message ?: "Unknown error")
                    }
                },
            )
        }
    }

    private fun loadData() {
        viewModel.getAppsWithNotificationPermissions()
    }
}
