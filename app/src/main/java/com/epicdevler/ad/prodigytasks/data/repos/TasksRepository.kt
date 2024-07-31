package com.epicdevler.ad.prodigytasks.data.repos

import com.epicdevler.ad.prodigytasks.data.models.Todo
import com.epicdevler.ad.prodigytasks.data.source.DataSource
import com.epicdevler.ad.prodigytasks.data.source.Response
import com.epicdevler.ad.prodigytasks.data.source.models.NewTodo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class TasksRepository(
    private val dataSource: DataSource
) {

    suspend fun create(task: String): Response<Nothing> {
        return dataSource.create(NewTodo(task))
    }

    suspend fun get(): Flow<Response<List<Todo>>> {
        return dataSource.get().map {
            if (it is Response.Success) {
                Response.Success(
                    it.data?.map { entity ->
                        Todo(
                            id = entity.id,
                            task = entity.task,
                            completed = entity.completed,
                            timeStamp = Date(entity.createDate)
                        )
                    },
                    message = it.message
                )
            } else {
                Response.Error(message = it.message)
            }
        }
    }

}