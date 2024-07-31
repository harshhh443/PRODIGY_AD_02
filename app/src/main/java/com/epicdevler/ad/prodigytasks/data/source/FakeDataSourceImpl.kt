package com.epicdevler.ad.prodigytasks.data.source

import android.content.res.Resources.NotFoundException
import com.epicdevler.ad.prodigytasks.data.source.db.TodoEntity
import com.epicdevler.ad.prodigytasks.data.source.models.NewTodo
import com.epicdevler.ad.prodigytasks.data.source.models.TodoId
import com.epicdevler.ad.prodigytasks.data.source.models.UpdateTodo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date

object FakeDataSourceImpl : DataSource{

    private val _data: MutableStateFlow<List<TodoEntity>> = MutableStateFlow(listOf())
    val data = _data.asStateFlow()
    private val rows = arrayListOf<TodoEntity>()
    private var lastId: Int = 0

    override suspend fun create(todo: NewTodo): Response<Nothing> {
        return run {
            try {
                val hasTask = rows.find {
                    it.task == todo.task
                }
                if (hasTask == null){
                    lastId++
                    rows.add(
                        TodoEntity(
                            id = lastId,
                            task = todo.task,
                            completed = false,
                            createDate = Date().time
                        )
                    )
                    _data.emit(rows)
                    Response.Success(message = "Task created.")
                }else{
                    throw Exception("task already existing")
                }
            }catch (e: Exception){
                Response.Error(message = e.message ?: e.localizedMessage)
            }
        }
    }

    override suspend fun update(todo: UpdateTodo): Response<Nothing> {
        return run {
            try {
                val hasItem = rows.find { it.id == todo.id.id }

                if (hasItem == null){
                    throw NotFoundException("")
                }else{
                    val updatedData = hasItem.copy(
                        task = todo.task,
                        completed = todo.completed
                    )
                    rows.set(rows.indexOf(hasItem), updatedData)

                    _data.emit(rows)
                    Response.Success(message = "Task updated.")
                }

            }catch (e: Exception){
                Response.Error(message = e.message ?: e.localizedMessage)
            }

        }
    }

    override suspend fun delete(todo: TodoId): Response<Nothing> {
        return run {
            try {
                val hasItem = rows.find { it.id == todo.id }

                    val remove = rows.remove(hasItem)
                    if (remove){


                        _data.emit(rows)
                        Response.Success(message = "Task deleted.")
                    }else{
                        throw Exception("Could not delete item with ($todo), item possibly doesn't not exists")
                    }

            }catch (e: Exception){
                Response.Error(message = e.message ?: e.localizedMessage)
            }

        }
    }

    override suspend fun get(): Flow<Response<List<TodoEntity>>> {
        return run {
            try {
                data.map {
                    Response.Success(it)
                }
            }catch (e: Exception){
                flowOf(Response.Error(message = e.message ?: e.localizedMessage))
            }
        }
    }

    private suspend fun  <T> run(task: suspend () -> T): T{
        return withContext(Dispatchers.IO){
            task()
        }
    }
}