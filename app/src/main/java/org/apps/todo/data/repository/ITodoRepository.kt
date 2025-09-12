package org.apps.todo.data.repository

import kotlinx.coroutines.flow.Flow
import org.apps.todo.data.local.TagEntity
import org.apps.todo.data.local.TodoEntity

interface ITodoRepository {
    // Tag
    fun getAllTags(): Flow<List<TagEntity>>
    suspend fun insertTag(tag: TagEntity): Long
    suspend fun deleteTag(tag: TagEntity)
    fun getTagById(tagId: Int): Flow<TagEntity>
    suspend fun insertTagReturnId(name: String): Int
    suspend fun updateTag(tag: TagEntity)

    // Todo
    fun getAllTodos(): Flow<List<TodoEntity>>
    fun getTodosByTag(tagId: Int): Flow<List<TodoEntity>>
    fun getTodosById(todoId: Int): Flow<TodoEntity>
    suspend fun insertTodo(todo: TodoEntity)
    suspend fun deleteTodo(todo: TodoEntity)
    suspend fun updateIsComplete(todoId: Int, isCompleted: Boolean)
    suspend fun updateTodo(todo: TodoEntity)
}
