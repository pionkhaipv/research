package pion.tech.pionbase.data.repository.notificationRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionDtoModel
import pion.tech.pionbase.data.model.notification.NotificationEntity
import pion.tech.pionbase.util.Result

interface NotificationRepository {
    fun getRecentNotifications(): Flow<Result<List<NotificationEntity>>>

    fun getAppsWithNotificationPermissions(): Flow<Result<List<AppNotificationPermissionDtoModel>>>

    fun toggleNotificationPermission(
        packageName: String,
        enabled: Boolean,
    ): Flow<Result<Boolean>>

    fun isNotificationListenerEnabled(): Flow<Result<Boolean>>

    fun setNotificationListenerEnabled(enabled: Boolean): Flow<Result<Boolean>>

    fun saveNotification(notification: NotificationEntity): Flow<Result<NotificationEntity>>

    fun sendTestNotification(): Flow<Result<Boolean>>
}
