package pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock.dialog

import android.os.Bundle
import com.piontech.core.base.BaseDialogFragment
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogToolTipBlockNotificationBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class ToolTipBlockNotificationDialog :
    BaseDialogFragment<DialogToolTipBlockNotificationBinding>(
        R.layout.dialog_tool_tip_block_notification,
    ) {
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.layoutOnGoingCalls.ivMain.setImageResource(R.drawable.ic_on_going_call)
        binding.layoutOnGoingCalls.tvFeatureName.text = getString(R.string.ongoing_calls)

        binding.layoutActive.ivMain.setImageResource(R.drawable.ic_active_service_noti)
        binding.layoutActive.tvFeatureName.text = getString(R.string.active_services)

        binding.layoutSystemWarning.ivMain.setImageResource(R.drawable.ic_system_warning_noti)
        binding.layoutSystemWarning.tvFeatureName.text =
            getString(R.string.system_warnings_low_battery_errors)
    }

    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.btnClose.setPreventDoubleClick {
            dismiss()
        }
    }
}
