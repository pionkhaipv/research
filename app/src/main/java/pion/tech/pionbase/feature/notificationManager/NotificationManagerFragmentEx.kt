package pion.tech.pionbase.feature.notificationManager

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import pion.tech.pionbase.feature.notificationManager.adapter.NotificationPagerAdapter
import pion.tech.pionbase.feature.notificationManager.dialog.RequestNotificationListenerPermissionDialog
import pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock.dialog.ToolTipBlockNotificationDialog
import pion.tech.pionbase.util.NotifyListenerManager
import pion.tech.pionbase.util.preventDrag
import pion.tech.pionbase.util.setPreventDoubleClick

fun NotificationManagerFragment.initView() {
    val adapter = NotificationPagerAdapter(this)
    binding.vpMain.adapter = adapter

    binding.vpMain.isUserInputEnabled = false

    viewModel.checkNotificationListenerStatus()
}

fun NotificationManagerFragment.toolTipEvent() {
    binding.btnBlockToolTip.setOnClickListener {
        ToolTipBlockNotificationDialog().show(childFragmentManager)
    }
}

fun NotificationManagerFragment.changeModeNotificationManagerEvent() {
    binding.btnNotificationStats.setPreventDoubleClick {
        viewModel.setModeNotificationManager(ModeNotificationManager.Stats)
    }

    binding.btnNotificationBlock.setPreventDoubleClick {
        viewModel.setModeNotificationManager(ModeNotificationManager.Block)
    }
}

fun NotificationManagerFragment.grandPermissionEvent() {
    binding.btnGrantPermission.setPreventDoubleClick {
        NotifyListenerManager.requestNotificationAccess(this)
    }
}

fun NotificationManagerFragment.setupUiForGrandPermission() {
    if (viewModel.isNotificationListenerEnabled.value) {
        binding.grLayoutContent.isVisible = true
        binding.svGrandPermission.isVisible = false
    } else {
        binding.grLayoutContent.isVisible = false
        binding.svGrandPermission.isVisible = true
        if (NotifyListenerManager.isGrandNotifyListenerPermission(requireContext())) {
            binding.clGrantPermission.isInvisible = true
            binding.clSwitchOnManager.isInvisible = false
        } else {
            binding.clGrantPermission.isInvisible = false
            binding.clSwitchOnManager.isInvisible = true
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun NotificationManagerFragment.setupSwitchListener() {
    binding.switchNotificationListener.preventDrag()
    binding.switchNotificationListener.setPreventDoubleClick {
        val isChecked = binding.switchNotificationListener.isChecked
        if (isChecked) {
            if (NotifyListenerManager.isGrandNotifyListenerPermission(requireContext())) {
                binding.switchNotificationListener.isChecked = true
                viewModel.toggleNotificationListener(true)
            } else {
                binding.switchNotificationListener.isChecked = false
                val dialog = RequestNotificationListenerPermissionDialog()
                dialog.setListener(
                    object : RequestNotificationListenerPermissionDialog.Listener {
                        override fun onOpenSetting() {
                            NotifyListenerManager.requestNotificationAccess(this@setupSwitchListener)
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
