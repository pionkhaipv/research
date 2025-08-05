package pion.tech.pionbase.feature.notificationManager

import pion.tech.pionbase.feature.notificationManager.adapter.NotificationPagerAdapter
import pion.tech.pionbase.feature.notificationManager.dialog.ToolTipBlockNotificationDialog
import pion.tech.pionbase.util.NotificationPermissionManager
import pion.tech.pionbase.util.setPreventDoubleClick

fun NotificationManagerFragment.initView() {
    val adapter = NotificationPagerAdapter(this)
    binding.vpMain.adapter = adapter

    binding.vpMain.isUserInputEnabled = false
}

fun NotificationManagerFragment.toolTipEvent() {
    binding.btnBlockToolTip.setOnClickListener {
        ToolTipBlockNotificationDialog().show(childFragmentManager)
    }
}

fun NotificationManagerFragment.changeModeNotificationManager() {
    binding.btnNotificationStats.setPreventDoubleClick {
        viewModel.setModeNotificationManager(ModeNotificationManager.Stats)
    }

    binding.btnNotificationBlock.setPreventDoubleClick {
        viewModel.setModeNotificationManager(ModeNotificationManager.Block)
    }
}

fun NotificationManagerFragment.setupSwitchListener() {
    binding.switchNotificationListener.setPreventDoubleClick {
        val isChecked = binding.switchNotificationListener.isChecked
        if (isChecked) {
            if (NotificationPermissionManager.areAllNotificationPermissionsGranted(requireContext())) {
                binding.switchNotificationListener.isChecked = true
                viewModel.toggleNotificationListener(true)
            } else {
                binding.switchNotificationListener.isChecked = false
            }
        } else {
            viewModel.toggleNotificationListener(false)
        }
    }
}

fun NotificationManagerFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
    binding.btnBack.setPreventDoubleClick {
        backEvent()
    }
}

fun NotificationManagerFragment.backEvent() {
    navigator.navigateUp()
}
