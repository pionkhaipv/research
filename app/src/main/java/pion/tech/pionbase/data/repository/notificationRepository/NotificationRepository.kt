package pion.tech.pionbase.data.repository.notificationRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.notification.NotificationDtoModel
import pion.tech.pionbase.data.model.notification.AppNotificationPermissionDtoModel
import pion.tech.pionbase.util.Result

interface NotificationRepository {
    fun getRecentNotifications(): Flow<Result<List<NotificationDtoModel>>>
    fun getAppsWithNotificationPermissions(): Flow<Result<List<AppNotificationPermissionDtoModel>>>
    fun toggleNotificationPermission(packageName: String, enabled: Boolean): Flow<Result<Boolean>>
    fun isNotificationListenerEnabled(): Flow<Result<Boolean>>
    fun setNotificationListenerEnabled(enabled: Boolean): Flow<Result<Boolean>>
}