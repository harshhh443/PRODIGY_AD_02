package com.epicdevler.ad.prodigytasks.data.source.models

data class NewTodo(
    val task: String,
)

data class UpdateTodo(
    val id: TodoId,
    val task: String,
    val completed: Boolean,
)

data class TodoId(
    val id: Int
)