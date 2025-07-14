package pion.tech.pionbase.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PopupDetectionEntity.TABLE_NAME)
data class PopupDetectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0,
    @ColumnInfo(name = APP_PACKAGE)
    val appPackage: String,
    @ColumnInfo(name = APP_NAME)
    val appName: String,
    @ColumnInfo(name = POPUP_TYPE)
    val popupType: String,
    @ColumnInfo(name = DETECTION_TIME)
    val detectionTime: Long,
    @ColumnInfo(name = CREATED_AT)
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        const val TABLE_NAME = "popup_detection"
        const val ID = "id"
        const val APP_PACKAGE = "app_package"
        const val APP_NAME = "app_name"
        const val POPUP_TYPE = "popup_type"
        const val DETECTION_TIME = "detection_time"
        const val CREATED_AT = "created_at"
    }
}
