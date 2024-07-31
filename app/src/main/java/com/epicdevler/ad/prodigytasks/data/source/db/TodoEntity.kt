package com.epicdevler.ad.prodigytasks.data.source.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Entity(tableName = "Todos")
data class TodoEntity(
    @PrimaryKey val id: Int = 0,
    val task: String,
    val completed: Boolean = false,
    val createDate: Long = Date().time,
)


@Dao
interface TodoDao {
    @Insert
    suspend fun create(todo: TodoEntity)

    @Query("SELECT * FROM Todos")
    fun get(): Flow<List<TodoEntity>>

    @Query("DELETE FROM Todos WHERE id == :id")
    suspend fun delete(id: Int)

}


@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
