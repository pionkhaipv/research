package pion.tech.pionbase.feature.notifications.presentation.model

import android.graphics.drawable.Drawable

data class NotificationAppInfo(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val notificationsEnabled: Boolean,
)
