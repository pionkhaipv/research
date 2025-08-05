package pion.tech.pionbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.data.dummy.DummyEntity
import pion.tech.pionbase.data.local.dao.DummyDAO
import pion.tech.pionbase.data.local.dao.NotificationDAO
import pion.tech.pionbase.data.local.dao.PopupDetectionDAO
import pion.tech.pionbase.data.model.notification.NotificationEntity
import pion.tech.pionbase.data.model.popupDetection.PopupDetectionEntity

@Database(entities = [DummyEntity::class, PopupDetectionEntity::class, NotificationEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDAO(): DummyDAO

    abstract fun popupDetectionDAO(): PopupDetectionDAO
    
    abstract fun notificationDAO(): NotificationDAO

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}
