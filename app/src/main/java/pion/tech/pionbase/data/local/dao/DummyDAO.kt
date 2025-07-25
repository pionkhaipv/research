package pion.tech.pionbase.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pion.tech.pionbase.data.dummy.DummyEntity

@Dao
interface DummyDAO {
    @Insert
    fun insert(vararg scale: DummyEntity)

    @Update
    fun update(vararg string: DummyEntity)

    @Delete
    fun delete(scale: DummyEntity)

    @Query("SELECT * FROM ${DummyEntity.Companion.TABLE_NAME} ORDER BY ${DummyEntity.Companion.ID} DESC")
    fun getAllDummies(): List<DummyEntity>?
}
