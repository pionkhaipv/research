package pion.tech.pionbase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.notification.NotificationEntity

@Dao
interface NotificationDAO {
    
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentNotifications(limit: Int = 50): Flow<List<NotificationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotification(notification: NotificationEntity): Long
    
    @Query("UPDATE notifications SET notificationCount = notificationCount + 1, timestamp = :newTimestamp WHERE packageName = :packageName")
    suspend fun incrementNotificationCount(packageName: String, newTimestamp: Long): Int
    
    @Query("SELECT * FROM notifications WHERE packageName = :packageName")
    suspend fun getNotificationByPackageName(packageName: String): NotificationEntity?
    
    @Query("DELETE FROM notifications WHERE packageName = :packageName")
    suspend fun deleteNotificationByPackageName(packageName: String)
    
    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()
    
    @Transaction
    suspend fun insertOrUpdateNotification(notification: NotificationEntity): NotificationEntity {
        // Try to update existing notification first
        val updatedRows = incrementNotificationCount(notification.packageName, notification.timestamp)
        
        return if (updatedRows > 0) {
            // Update was successful, retrieve and return the updated notification
            // Use safe call instead of force unwrap to handle edge cases
            getNotificationByPackageName(notification.packageName) ?: run {
                // Fallback: if somehow the notification was deleted after update, insert new one
                insertNotification(notification)
                notification
            }
        } else {
            // No existing notification found, insert new one
            val insertResult = insertNotification(notification)
            if (insertResult > 0) {
                // Insert was successful, return the notification
                notification
            } else {
                // Insert failed (probably due to conflict), try to get existing one
                getNotificationByPackageName(notification.packageName) ?: notification
            }
        }
    }
}