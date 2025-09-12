package org.apps.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.apps.todo.data.local.TodoDao
import org.apps.todo.data.local.TodoEntity

class FakeTodoDao: TodoDao {
    private val todos = mutableListOf<TodoEntity>()
    private val flow = MutableStateFlow<List<TodoEntity>>(emptyList())

    override fun getAllTodos(): Flow<List<TodoEntity>> = flow

    override suspend fun insertTodo(todo: TodoEntity) {
        todos.add(todo.copy(id = todos.size + 1))
        flow.value = todos
    }

    override suspend fun deleteTodo(todo: TodoEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTodo(todo: TodoEntity) {
        TODO("Not yet implemented")
    }

    override fun getTodosByTag(tagId: Int): Flow<List<TodoEntity>> {
        TODO("Not yet implemented")
    }

    override fun getTodosById(id: Int): Flow<TodoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun updateIsDone(id: Int, isDone: Boolean) {
        TODO("Not yet implemented")
    }


}