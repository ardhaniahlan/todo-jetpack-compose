package org.apps.todo.data.repository

import kotlinx.coroutines.flow.Flow
import org.apps.todo.data.local.TagDao
import org.apps.todo.data.local.TagEntity
import org.apps.todo.data.local.TodoDao
import org.apps.todo.data.local.TodoEntity

class TodoRepositoryImpl(
    private val tagDao: TagDao,
    private val todoDao: TodoDao
) : ITodoRepository {
    // Tag
    override fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()
    override suspend fun insertTag(tag: TagEntity) = tagDao.insertTag(tag)
    override suspend fun deleteTag(tag: TagEntity) = tagDao.deleteTag(tag)
    override fun getTagById(tagId: Int): Flow<TagEntity> = tagDao.getTagById(tagId)
    override suspend fun insertTagReturnId(name: String): Int {
        return tagDao.insertTag(TagEntity(name = name)).toInt()
    }
    override suspend fun updateTag(tag: TagEntity) = tagDao.updateTag(tag)


    // Todo
    override fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()
    override fun getTodosByTag(tagId: Int): Flow<List<TodoEntity>> = todoDao.getTodosByTag(tagId)
    override fun getTodosById(todoId: Int): Flow<TodoEntity> = todoDao.getTodosById(todoId)
    override suspend fun insertTodo(todo: TodoEntity) = todoDao.insertTodo(todo)
    override suspend fun deleteTodo(todo: TodoEntity) = todoDao.deleteTodo(todo)
    override suspend fun updateIsComplete(todoId: Int, isCompleted: Boolean) = todoDao.updateIsDone(todoId, isCompleted)
    override suspend fun updateTodo(todo: TodoEntity) = todoDao.updateTodo(todo)
}
