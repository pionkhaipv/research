package pion.tech.pionbase.feature.notificationManager.notificationPermissions

import pion.tech.pionbase.feature.notificationManager.adapter.AppNotificationPermissionAdapter

fun NotificationPermissionsFragment.initView() {
    // Initialize RecyclerView and adapter
    appPermissionsAdapter =
        AppNotificationPermissionAdapter { packageName, enabled ->
            // Handle switch toggle events
            viewModel.toggleAppNotificationPermission(packageName, enabled)
        }
    binding.rvAppPermissions.adapter = appPermissionsAdapter
}
