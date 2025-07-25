package pion.tech.pionbase.data.dummy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pion.tech.pionbase.data.dummy.DummyUiModel

@Entity(tableName = DummyEntity.TABLE_NAME)
data class DummyEntity(
    @PrimaryKey
    @ColumnInfo(name = ID)
    val id: Long,
    @ColumnInfo(name = VALUE)
    val value: String,
) {
    companion object {
        const val TABLE_NAME = "DummyEntity"
        const val ID = "ID"
        const val VALUE = "VALUE"
    }
}

fun DummyEntity.toDomain(): DummyUiModel = DummyUiModel(id = this.id, value = this.value)
