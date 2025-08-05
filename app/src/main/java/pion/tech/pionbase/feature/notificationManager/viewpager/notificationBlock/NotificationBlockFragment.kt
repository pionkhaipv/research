package pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock

import android.view.View
import androidx.core.view.isVisible
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionUIModel
import pion.tech.pionbase.databinding.FragmentNotificationBlockBinding
import pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock.adapter.AppNotificationBlockAdapter
import pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock.dialog.ConfirmBlockNotificationDialog
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState
import timber.log.Timber

@AndroidEntryPoint
class NotificationBlockFragment :
    BaseFragment<FragmentNotificationBlockBinding, NotificationBlockViewModel, CommonViewModel>(
        FragmentNotificationBlockBinding::inflate,
        NotificationBlockViewModel::class.java,
        CommonViewModel::class.java,
    ),
    AppNotificationBlockAdapter.Listener {
    var adapter = AppNotificationBlockAdapter()

    override fun init(view: View) {
        initView()
        searchEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.appFilteredList.collectFlowOnView(viewLifecycleOwner) {
            val tag = "appFilteredList"
            binding.lottieLoading.pauseAnimation()
            binding.lottieLoading.isVisible = false
            Timber.tag(tag).d(it.toString())
            it.handleUiState(
                onLoading = {
                    binding.lottieLoading.playAnimation()
                    binding.lottieLoading.isVisible = true
                },
                onSuccess = { apps ->
                    adapter.submitList(apps)
                },
                onError = { exception ->
                    displayToast(getString(R.string.failed_to_load, exception.message))
                },
            )
        }
    }

    override fun onTogglePermission(
        item: AppNotificationPermissionUIModel,
        isEnabled: Boolean,
    ) {
        val tag = "onTogglePermission"
        val handlerToggleError = { exception: Throwable ->
            Timber.tag(tag).d("onToggleError: $exception")
            displayToast("Fail to update block notification permission")
        }
        if (!isEnabled) {
            val dialog = ConfirmBlockNotificationDialog()
            dialog.setListener(
                object : ConfirmBlockNotificationDialog.Listener {
                    override fun onConfirmBlock() {
                        viewModel.toggleAppNotificationPermission(
                            packageName = item.packageName,
                            enabled = isEnabled,
                            onError = { handlerToggleError.invoke(it) },
                        )
                    }
                },
            )
            dialog.show(childFragmentManager)
        } else {
            viewModel.toggleAppNotificationPermission(
                packageName = item.packageName,
                enabled = isEnabled,
                onError = { handlerToggleError.invoke(it) },
            )
        }
    }
}
