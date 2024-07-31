package com.epicdevler.ad.prodigytasks.data.source

import com.epicdevler.ad.prodigytasks.data.source.db.TodoDao
import com.epicdevler.ad.prodigytasks.data.source.db.TodoEntity
import com.epicdevler.ad.prodigytasks.data.source.models.NewTodo
import com.epicdevler.ad.prodigytasks.data.source.models.TodoId
import com.epicdevler.ad.prodigytasks.data.source.models.UpdateTodo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class MainDataSourceImpl(private val db: TodoDao) : DataSource {


    override suspend fun create(todo: NewTodo): Response<Nothing> {
        return run {
            try {
                db.create(
                    TodoEntity(
                        task = todo.task
                    )
                )
                Response.Success(message = "Task created.")
            } catch (e: Exception) {
                Response.Error(message = e.message ?: e.localizedMessage)
            }
        }
    }

    override suspend fun update(todo: UpdateTodo): Response<Nothing> {
        return run {
            try {
                Response.Success(message = "Task updated.")

            } catch (e: Exception) {
                Response.Error(message = e.message ?: e.localizedMessage)
            }

        }
    }

    override suspend fun delete(todo: TodoId): Response<Nothing> {
        return run {
            try {
                db.delete(todo.id)
                Response.Success(message = "Task deleted")
            } catch (e: Exception) {
                Response.Error(message = e.message ?: e.localizedMessage)
            }

        }
    }

    override suspend fun get(): Flow<Response<List<TodoEntity>>> {
        return callbackFlow {
            run {
                try {
                    db.get().collect {
                        Response.Success(
                            data = it
                        )
                    }
                } catch (e: Exception) {
                    trySendBlocking(Response.Error(message = e.message ?: e.localizedMessage))
                }
            }
        }
    }

    private suspend fun <T> run(task: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            task()
        }
    }
}