package pion.tech.pionbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.data.dummy.DummyEntity
import pion.tech.pionbase.data.local.dao.DummyDAO
import pion.tech.pionbase.data.local.dao.PopupDetectionDAO
import pion.tech.pionbase.data.model.popupDetection.PopupDetectionEntity

@Database(entities = [DummyEntity::class, PopupDetectionEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDAO(): DummyDAO

    abstract fun popupDetectionDAO(): PopupDetectionDAO

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}
