package org.apps.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.apps.todo.data.local.TagEntity
import org.apps.todo.data.local.TodoEntity
import org.apps.todo.data.repository.ITodoRepository

class FakeTodoRepository : ITodoRepository {
    private val todos = mutableListOf<TodoEntity>()
    private val todosFlow = MutableStateFlow<List<TodoEntity>>(emptyList())

    override fun getAllTodos(): Flow<List<TodoEntity>> = todosFlow
    override suspend fun insertTodo(todo: TodoEntity) {
        todos.add(todo.copy(id = todos.size + 1))
        todosFlow.value = todos
    }

    // Implementasi minimal biar test jalan
    override fun getTodosByTag(tagId: Int): Flow<List<TodoEntity>> = todosFlow
    override fun getTodosById(todoId: Int): Flow<TodoEntity> =
        todosFlow.map { list -> list.first { it.id == todoId } }
    override suspend fun deleteTodo(todo: TodoEntity) {}
    override suspend fun updateIsComplete(todoId: Int, isCompleted: Boolean) {}
    override suspend fun updateTodo(todo: TodoEntity) {}

    // Tag (dummy)
    override fun getAllTags(): Flow<List<TagEntity>> = flowOf(emptyList())
    override suspend fun insertTag(tag: TagEntity): Long = 1
    override suspend fun deleteTag(tag: TagEntity) {}
    override fun getTagById(tagId: Int): Flow<TagEntity> = flowOf(TagEntity(1, "Dummy"))
    override suspend fun insertTagReturnId(name: String): Int = 1
    override suspend fun updateTag(tag: TagEntity) {}
}
