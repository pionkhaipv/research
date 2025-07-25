package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable

data class AppNotificationPermissionDtoModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isNotificationEnabled: Boolean,
)