package pion.tech.pionbase.data.model.notification

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
    val notificationCount: Int = 1
)

// Extension function to convert DTO to Entity
fun NotificationDtoModel.toEntity(): NotificationEntity {
    return NotificationEntity(
        packageName = this.packageName,
        appName = this.appName,
        title = this.title,
        content = this.content,
        timestamp = this.timestamp,
        notificationCount = 1
    )
}

// Extension function to convert Entity to DTO (for presentation layer)
fun NotificationEntity.toPresentation(): NotificationDtoModel {
    return NotificationDtoModel(
        packageName = this.packageName,
        appName = this.appName,
        icon = null, // Icon will be loaded separately if needed
        title = this.title,
        content = this.content,
        timestamp = this.timestamp
    )
}