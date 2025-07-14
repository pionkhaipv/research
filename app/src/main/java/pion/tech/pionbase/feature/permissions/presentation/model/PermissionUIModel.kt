package pion.tech.pionbase.feature.permissions.presentation.model

import android.graphics.drawable.Drawable

data class PermissionUIModel(
    val permissionName: String,
    val displayName: String,
    val description: String,
    val dangerLevel: DangerLevel,
    val appsWithPermission: List<AppPermissionUIModel>,
)

data class AppPermissionUIModel(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val isSystemApp: Boolean,
    val isGranted: Boolean,
)

enum class DangerLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
}
