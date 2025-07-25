package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable

data class AppNotificationPermissionUIModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isNotificationEnabled: Boolean,
)

fun AppNotificationPermissionDtoModel.toPresentation(): AppNotificationPermissionUIModel =
    AppNotificationPermissionUIModel(
        packageName = this.packageName,
        appName = this.appName,
        icon = this.icon,
        isNotificationEnabled = this.isNotificationEnabled,
    )