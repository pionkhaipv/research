package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable

data class NotificationUIModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val title: String?,
    val content: String?,
    val timestamp: Long,
    val notificationCount: Int,
)
