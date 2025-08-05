package pion.tech.pionbase.feature.home

import pion.tech.pionbase.R
import pion.tech.pionbase.util.NotifyListenerManager
import pion.tech.pionbase.util.RunningAppsPermissionManager
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    binding.btnCheckHiddenApps.tvFeatureName.text = getString(R.string.check_hidden_apps)
    binding.btnNotificationControl.tvFeatureName.text = getString(R.string.notification_control)
    binding.btnHibernateApp.tvFeatureName.text = getString(R.string.hibernate_app)
    binding.btnShowConcern.tvFeatureName.text = getString(R.string.show_concerns)

    binding.btnCheckHiddenApps.ivIconFeature.setImageResource(R.drawable.ic_check_hidden_app_home)
    binding.btnNotificationControl.ivIconFeature.setImageResource(R.drawable.ic_notification_control_home)
    binding.btnHibernateApp.ivIconFeature.setImageResource(R.drawable.ic_hibernate_app_home)
    binding.btnShowConcern.ivIconFeature.setImageResource(R.drawable.ic_show_concern_home)
}

fun HomeFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
}

fun HomeFragment.checkHiddenAppsEvent() {
    binding.btnCheckHiddenApps.root.setPreventDoubleClick {
        navigator.navigateTo(R.id.action_homeFragment_to_scanHiddenAppFragment)
    }
}

fun HomeFragment.showConcernEvent() {
    binding.btnShowConcern.root.setPreventDoubleClick {
        navigator.navigateTo(R.id.action_homeFragment_to_scanConcernAppFragment)
    }
}

fun HomeFragment.settingEvent() {
    binding.btnSetting.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_settingFragment)
    }
}

fun HomeFragment.notificationManagerEvent() {
    binding.btnNotificationControl.root.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_notificationManagerFragment)
    }
}

fun HomeFragment.adDetectorEvent() {
    binding.btnPopupDetector.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_popupStatisticsFragment)
    }
}
