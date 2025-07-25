package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable

data class NotificationUIModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val title: String?,
    val content: String?,
    val timestamp: Long,
)

fun NotificationDtoModel.toPresentation(): NotificationUIModel =
    NotificationUIModel(
        packageName = this.packageName,
        appName = this.appName,
        icon = this.icon,
        title = this.title,
        content = this.content,
        timestamp = this.timestamp,
    )