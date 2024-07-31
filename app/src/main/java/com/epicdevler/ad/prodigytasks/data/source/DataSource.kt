package com.epicdevler.ad.prodigytasks.data.source

import com.epicdevler.ad.prodigytasks.data.source.db.TodoEntity
import com.epicdevler.ad.prodigytasks.data.source.models.NewTodo
import com.epicdevler.ad.prodigytasks.data.source.models.TodoId
import com.epicdevler.ad.prodigytasks.data.source.models.UpdateTodo
import kotlinx.coroutines.flow.Flow

interface DataSource {

    suspend fun create(todo: NewTodo): Response<Nothing>

    suspend fun update(todo: UpdateTodo): Response<Nothing>

    suspend fun delete(todo: TodoId): Response<Nothing>

    suspend fun get(): Flow<Response<List<TodoEntity>>>

}

sealed class Response<T>(val data: T? = null, val message: String = "") {
    class Success<T>(data: T? = null, message: String = "") :
        Response<T>(data = data, message = message)

    class Error<T>(data: T? = null, message: String = "") :
        Response<T>(data = data, message = message)
}