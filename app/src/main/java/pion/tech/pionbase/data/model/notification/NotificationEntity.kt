package pion.tech.pionbase.data.model.notification

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val title: String?,
    val content: String?,
    val timestamp: Long,
    val notificationCount: Int = 1,
)

// Extension function to convert Entity to UIModel (for presentation layer)
fun NotificationEntity.toPresentation(icon: Drawable? = null): NotificationUIModel =
    NotificationUIModel(
        packageName = this.packageName,
        appName = this.appName,
        icon = icon, // Icon will be loaded separately if needed
        title = this.title,
        content = this.content,
        timestamp = this.timestamp,
        notificationCount = this.notificationCount,
    )
