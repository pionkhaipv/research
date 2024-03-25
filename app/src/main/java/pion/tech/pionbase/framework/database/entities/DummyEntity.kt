package pion.tech.pionbase.framework.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pion.tech.pionbase.framework.database.entities.DummyEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class DummyEntity(
    @PrimaryKey
    @ColumnInfo(name = ID)
    val id : Long,

    @ColumnInfo(name = VALUE)
    val value : String
){

    companion object{
        const val TABLE_NAME = "DummyEntity"
        const val ID = "ID"
        const val VALUE = "VALUE"
    }
}
