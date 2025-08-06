package pion.tech.pionbase.feature.notificationManager.viewpager.notificationStats

import android.view.View
import androidx.core.view.isVisible
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentRecentNotificationsBinding
import pion.tech.pionbase.feature.notificationManager.viewpager.notificationStats.adapter.NotificationStatsAdapter
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class NotificationStatsFragment :
    BaseFragment<FragmentRecentNotificationsBinding, NotificationStatsViewModel, CommonViewModel>(
        FragmentRecentNotificationsBinding::inflate,
        NotificationStatsViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    val adapter = NotificationStatsAdapter()

    override fun init(view: View) {
        initView()
    }

    override fun subscribeObserver(view: View) {
        viewModel.recentNotificationsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    binding.rvMain.isVisible = false
                    binding.llEmptyLoading.isVisible = true
                },
                onSuccess = { notifications ->
                    if (notifications.isEmpty()) {
                        binding.rvMain.isVisible = false
                        binding.llEmptyLoading.isVisible = true
                    } else {
                        adapter.submitList(notifications)
                        binding.rvMain.isVisible = true
                        binding.llEmptyLoading.isVisible = false
                    }
                },
                onError = { exception ->
                    binding.rvMain.isVisible = false
                    binding.llEmptyLoading.isVisible = true
                    displayToast(
                        getString(
                            R.string.failed_to_load_recent_notifications,
                            exception.message,
                        ),
                    )
                },
            )
        }
    }
}
