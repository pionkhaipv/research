package pion.tech.pionbase.feature.notificationManager

import android.view.View
import androidx.core.graphics.toColorInt
import androidx.core.view.isInvisible
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
        initView()
        setupSwitchListener()
        changeModeNotificationManager()
        onBackEvent()
        toolTipEvent()
        viewModel.checkNotificationListenerStatus()
    }

    override fun subscribeObserver(view: View) {
        viewModel.isNotificationListenerEnabled.collectFlowOnView(viewLifecycleOwner) { isEnabled ->
            binding.switchNotificationListener.isChecked = isEnabled
        }
        viewModel.modeNotificationManager.collectFlowOnView(viewLifecycleOwner) {
            binding.tvNotificationStats.setTextColor("#4B4C53".toColorInt())
            binding.tvBlock.setTextColor("#4B4C53".toColorInt())
            binding.viewBottomBlock.isInvisible = true
            binding.viewBottomStats.isInvisible = true
            when (it) {
                ModeNotificationManager.Stats -> {
                    binding.vpMain.currentItem = 0
                    binding.tvNotificationStats.setTextColor("#43BDFF".toColorInt())
                    binding.viewBottomStats.isInvisible = false
                }

                ModeNotificationManager.Block -> {
                    binding.vpMain.currentItem = 1
                    binding.tvBlock.setTextColor("#43BDFF".toColorInt())
                    binding.viewBottomBlock.isInvisible = false
                }
            }
        }
    }
}
