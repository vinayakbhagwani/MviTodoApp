package com.demo.mvitodoapp.model.repository

import com.demo.mvitodoapp.model.local.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun insert(todo: Todo)

    suspend fun delete(todo: Todo)

    suspend fun update(todo: Todo)

    fun getAllTodoList(): Flow<List<Todo>>
}