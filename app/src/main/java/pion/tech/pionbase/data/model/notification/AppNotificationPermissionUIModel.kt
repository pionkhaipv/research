package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable

data class AppNotificationPermissionUIModel(
    val packageName: String,
    val appName: String,
    val isNotificationEnabled: Boolean,
    val appIcon: Drawable? = null,
)

fun AppNotificationPermissionDtoModel.toPresentation(): AppNotificationPermissionUIModel =
    AppNotificationPermissionUIModel(
        packageName = this.packageName,
        appName = this.appName,
        isNotificationEnabled = this.isNotificationEnabled,
        appIcon = this.appIcon,
    )
