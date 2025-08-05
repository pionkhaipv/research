package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable

data class AppNotificationPermissionDtoModel(
    val packageName: String,
    val appName: String,
    val isNotificationEnabled: Boolean,
    val appIcon: Drawable? = null,
)
