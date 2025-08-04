package pion.tech.pionbase.feature.home

import pion.tech.pionbase.R
import pion.tech.pionbase.feature.home.bottomSheet.DemoBottomSheet
import pion.tech.pionbase.util.NotificationPermissionManager
import pion.tech.pionbase.util.RunningAppsPermissionManager
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    commonViewModel.getApiData()
}

fun HomeFragment.plusEvent() {
    val listString = listOf("so1", "so2", "so3", "so4", "so5")
    adapter.submitList(listString)
}

fun HomeFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
}

fun HomeFragment.checkHiddenAppsEvent() {
    binding.btnCheckHiddenApps.setPreventDoubleClick {
        navigator.navigateTo(R.id.action_homeFragment_to_scanHiddenAppFragment)
    }
}

fun HomeFragment.showConcernEvent() {
    binding.btnShowConcern.setPreventDoubleClick {
        navigator.navigateTo(R.id.action_homeFragment_to_scanConcernAppFragment)
    }
}

fun HomeFragment.settingEvent() {
    binding.btnSetting.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_settingFragment)
    }
}

fun HomeFragment.notificationManagerEvent() {
    binding.btnNotificationManager.setPreventDoubleClickScaleView {
        checkNotificationPermissionsAndNavigate()
    }
}

fun HomeFragment.runningAppsEvent() {
    binding.btnRunningApps.setPreventDoubleClickScaleView {
        checkRunningAppsPermissionsAndNavigate()
    }
}

fun HomeFragment.checkNotificationPermissionsAndNavigate() {
    if (NotificationPermissionManager.areAllNotificationPermissionsGranted(requireContext())) {
        // All permissions granted, navigate to notification manager
        navigator.navigateTo(R.id.action_homeFragment_to_notificationManagerFragment)
    } else {
        // Permissions not granted, show dialog and request permissions
        val missingPermissions =
            NotificationPermissionManager.getMissingNotificationPermissions(requireContext())
        val permissionMessage =
            "To use Notification Manager, please grant the following permissions:\n\n" +
                missingPermissions.joinToString("\n• ", "• ")

        displayToast("$permissionMessage\n\nOpening settings...")

        // Request notification access permission
        NotificationPermissionManager.requestNotificationAccess(this)

        // Set flag to check permissions when user returns
        setWaitingForPermissions(true)
    }
}

fun HomeFragment.setWaitingForPermissions(waiting: Boolean) {
    // Store the waiting state in a simple way
    // We'll check this when the fragment resumes
    viewModel.setWaitingForNotificationPermissions(waiting)
}

fun HomeFragment.checkPermissionsOnResume() {
    if (viewModel.isWaitingForNotificationPermissions()) {
        viewModel.setWaitingForNotificationPermissions(false)

        // Check if permissions are now granted
        if (NotificationPermissionManager.areAllNotificationPermissionsGranted(requireContext())) {
            displayToast("Permissions granted! Opening Notification Manager...")
            navigator.navigateTo(R.id.action_homeFragment_to_notificationManagerFragment)
        } else {
            displayToast("Permissions are still required to use Notification Manager")
        }
    }

    if (viewModel.isWaitingForRunningAppsPermissions()) {
        viewModel.setWaitingForRunningAppsPermissions(false)

        // Check if permissions are now granted
        if (RunningAppsPermissionManager.areAllRunningAppsPermissionsGranted(requireContext())) {
            displayToast("Permissions granted! Opening Running Apps...")
            navigator.navigateTo(R.id.action_homeFragment_to_runningAppsFragment)
        } else {
            displayToast("Permissions are still required to use Running Apps Manager")
        }
    }
}

fun HomeFragment.adDetectorEvent() {
    binding.btnAdDetector.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_popupStatisticsFragment)
    }
}

fun HomeFragment.checkRunningAppsPermissionsAndNavigate() {
    if (RunningAppsPermissionManager.areAllRunningAppsPermissionsGranted(requireContext())) {
        // All permissions granted, navigate to running apps
        navigator.navigateTo(R.id.action_homeFragment_to_runningAppsFragment)
    } else {
        // Permissions not granted, show dialog and request permissions
        val missingPermissions =
            RunningAppsPermissionManager.getMissingRunningAppsPermissions(requireContext())
        val permissionMessage =
            if (missingPermissions.isNotEmpty()) {
                "To use Running Apps Manager, please grant the following permissions:\n\n" +
                    missingPermissions.joinToString("\n• ", "• ")
            } else {
                RunningAppsPermissionManager.getPermissionExplanation()
            }

        displayToast("$permissionMessage\n\nOpening settings...")

        // Request running apps permissions
        RunningAppsPermissionManager.requestRunningAppsPermissions(this)

        // Set flag to check permissions when user returns
        setWaitingForRunningAppsPermissions(true)
    }
}

fun HomeFragment.setWaitingForRunningAppsPermissions(waiting: Boolean) {
    // Store the waiting state in a simple way
    // We'll check this when the fragment resumes
    viewModel.setWaitingForRunningAppsPermissions(waiting)
}
