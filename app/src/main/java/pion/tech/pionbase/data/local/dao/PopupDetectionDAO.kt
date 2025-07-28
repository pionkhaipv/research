package pion.tech.pionbase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.popupDetection.PopupDetectionEntity

@Dao
interface PopupDetectionDAO {
    @Insert
    suspend fun insert(popupDetection: PopupDetectionEntity): Long

    @Query("SELECT * FROM ${PopupDetectionEntity.TABLE_NAME} ORDER BY ${PopupDetectionEntity.DETECTION_TIME} DESC")
    fun getAllPopupDetections(): Flow<List<PopupDetectionEntity>>

    @Query(
        "SELECT * FROM ${PopupDetectionEntity.TABLE_NAME} WHERE ${PopupDetectionEntity.APP_PACKAGE} = :packageName ORDER BY ${PopupDetectionEntity.DETECTION_TIME} DESC",
    )
    fun getPopupDetectionsByPackage(packageName: String): Flow<List<PopupDetectionEntity>>

    @Query("SELECT COUNT(*) FROM ${PopupDetectionEntity.TABLE_NAME} WHERE ${PopupDetectionEntity.APP_PACKAGE} = :packageName")
    fun getPopupCountByPackage(packageName: String): Flow<Int>

    @Query("DELETE FROM ${PopupDetectionEntity.TABLE_NAME} WHERE ${PopupDetectionEntity.DETECTION_TIME} < :beforeTime")
    suspend fun deleteOldRecords(beforeTime: Long): Int

    @Query(
        "SELECT * FROM ${PopupDetectionEntity.TABLE_NAME} WHERE ${PopupDetectionEntity.DETECTION_TIME} BETWEEN :startTime AND :endTime ORDER BY ${PopupDetectionEntity.DETECTION_TIME} DESC",
    )
    fun getPopupDetectionsByTimeRange(
        startTime: Long,
        endTime: Long,
    ): Flow<List<PopupDetectionEntity>>

    @Query("SELECT COUNT(*) FROM ${PopupDetectionEntity.TABLE_NAME}")
    fun getTotalPopupCount(): Flow<Int>

    @Query(
        "SELECT DISTINCT ${PopupDetectionEntity.APP_PACKAGE} as appPackage, ${PopupDetectionEntity.APP_NAME} as appName FROM ${PopupDetectionEntity.TABLE_NAME} ORDER BY ${PopupDetectionEntity.APP_NAME}",
    )
    fun getUniqueAppsWithPopups(): Flow<List<AppInfo>>

    @Query(
        "SELECT ${PopupDetectionEntity.POPUP_TYPE} as popupType, COUNT(*) as count FROM ${PopupDetectionEntity.TABLE_NAME} GROUP BY ${PopupDetectionEntity.POPUP_TYPE} ORDER BY count DESC",
    )
    fun getPopupTypeStatistics(): Flow<List<PopupTypeStatistic>>

    @Query("DELETE FROM ${PopupDetectionEntity.TABLE_NAME} WHERE ${PopupDetectionEntity.APP_PACKAGE} = :packageName")
    suspend fun deleteByPackage(packageName: String): Int

    @Query("DELETE FROM ${PopupDetectionEntity.TABLE_NAME}")
    suspend fun deleteAll(): Int
}

data class PopupTypeStatistic(
    val popupType: String,
    val count: Int,
)

data class AppInfo(
    val appPackage: String,
    val appName: String,
)
