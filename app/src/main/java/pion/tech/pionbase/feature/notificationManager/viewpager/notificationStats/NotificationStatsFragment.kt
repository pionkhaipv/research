package pion.tech.pionbase.feature.notificationManager.viewpager.notificationStats

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentRecentNotificationsBinding
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class NotificationStatsFragment :
    BaseFragment<FragmentRecentNotificationsBinding, NotificationStatsViewModel, CommonViewModel>(
        FragmentRecentNotificationsBinding::inflate,
        NotificationStatsViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    override fun init(view: View) {
        initView()
        loadData()
    }

    override fun subscribeObserver(view: View) {
        viewModel.recentNotificationsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    // Show loading state
                },
                onSuccess = { notifications ->
                    // Handle the list of recent notifications
                    // Update RecyclerView adapter here
                },
                onError = { exception ->
                },
            )
        }
    }

    private fun loadData() {
        viewModel.getRecentNotifications()
    }
}
