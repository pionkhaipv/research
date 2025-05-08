package pion.tech.pionbase.app.data.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.app.data.roomDb.dao.DummyDAO
import pion.tech.pionbase.app.data.model.DummyEntity

@Database(entities = [DummyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun dummyDAO(): DummyDAO

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}