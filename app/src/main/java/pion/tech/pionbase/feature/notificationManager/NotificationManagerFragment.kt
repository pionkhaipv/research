package pion.tech.pionbase.feature.notificationManager

import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentNotificationManagerBinding
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class NotificationManagerFragment :
    BaseFragment<FragmentNotificationManagerBinding, NotificationManagerViewModel, CommonViewModel>(
        FragmentNotificationManagerBinding::inflate,
        NotificationManagerViewModel::class.java,
        CommonViewModel::class.java,
    ) {

    override fun init(view: View) {
        logger.logScreen("notification_manager_show")
        logger.logEvent("notification_manager_view")
        initView()
        setupViewPager()
        setupSwitchListener()
        onBackEvent()

        // Load initial data
        viewModel.checkNotificationListenerStatus()
    }

    override fun subscribeObserver(view: View) {
        viewModel.isNotificationListenerEnabled.collectFlowOnView(viewLifecycleOwner) { isEnabled ->
            binding.switchNotificationListener.isChecked = isEnabled
        }
    }
}
