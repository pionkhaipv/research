package pion.tech.pionbase.feature.notificationManager

import android.annotation.SuppressLint
import android.view.MotionEvent
import pion.tech.pionbase.feature.notificationManager.adapter.NotificationPagerAdapter
import pion.tech.pionbase.feature.notificationManager.dialog.RequestNotificationListenerPermissionDialog
import pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock.dialog.ToolTipBlockNotificationDialog
import pion.tech.pionbase.util.NotifyListenerPermissionManager
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

@SuppressLint("ClickableViewAccessibility")
fun NotificationManagerFragment.setupSwitchListener() {
    binding.switchNotificationListener.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_MOVE) {
            // Chặn drag
            true
        } else {
            // Cho phép xử lý bình thường các sự kiện khác (click)
            false
        }
    }
    binding.switchNotificationListener.setPreventDoubleClick {
        val isChecked = binding.switchNotificationListener.isChecked
        if (isChecked) {
            if (NotifyListenerPermissionManager.areAllNotificationPermissionsGranted(requireContext())) {
                binding.switchNotificationListener.isChecked = true
                viewModel.toggleNotificationListener(true)
            } else {
                binding.switchNotificationListener.isChecked = false
                val dialog = RequestNotificationListenerPermissionDialog()
                dialog.setListener(
                    object : RequestNotificationListenerPermissionDialog.Listener {
                        override fun onOpenSetting() {
                            NotifyListenerPermissionManager.requestNotificationAccess(this@setupSwitchListener)
                        }
                    },
                )
                dialog.show(childFragmentManager)
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
