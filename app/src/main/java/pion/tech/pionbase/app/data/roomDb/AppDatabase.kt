package pion.tech.pionbase.app.data.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.app.data.model.DummyEntity
import pion.tech.pionbase.app.data.model.PopupDetectionEntity
import pion.tech.pionbase.app.data.roomDb.dao.DummyDAO
import pion.tech.pionbase.app.data.roomDb.dao.PopupDetectionDAO

@Database(entities = [DummyEntity::class, PopupDetectionEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDAO(): DummyDAO

    abstract fun popupDetectionDAO(): PopupDetectionDAO

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}
