package com.epicdevler.ad.prodigytasks.data.models

import java.util.Calendar
import java.util.Date

data class Todo(
    val id: Int,
    val task: String,
    val completed: Boolean,
    val timeStamp: Date
) {
    companion object {
        private var index = 0
        val dummyTodos = arrayListOf<Todo>()

        init {
            while (index < 10) {
                index++
                dummyTodos.add(
                    Todo(
                        id = index,
                        task = "Task $index",
                        completed = index % 2 == 0,
                        timeStamp = Calendar.getInstance().time
                    )
                )
            }
        }
    }
}