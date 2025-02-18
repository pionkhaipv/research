package com.piontech.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.piontech.data.local.db.dao.DummyDAO
import com.piontech.data.model.DummyEntity

@Database(entities = [DummyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun dummyDAO(): DummyDAO

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}