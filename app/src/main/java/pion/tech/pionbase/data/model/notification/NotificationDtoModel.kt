package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable

data class NotificationDtoModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val title: String?,
    val content: String?,
    val timestamp: Long,
)