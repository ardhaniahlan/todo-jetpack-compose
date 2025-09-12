package org.apps.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.apps.todo.data.repository.ITodoRepository
import org.apps.todo.data.local.TagEntity
import org.apps.todo.data.local.TodoEntity

class TodoViewModel(
    private val repository: ITodoRepository
) : ViewModel() {

    val allTags = repository.getAllTags().asLiveData()
    val allTodos = repository.getAllTodos().asLiveData()

    fun getTodosByTag(tagId: Int) = repository.getTodosByTag(tagId).asLiveData()
    fun getTodosById(todoId: Int) = repository.getTodosById(todoId).asLiveData()
    fun getTagById(tagId: Int) = repository.getTagById(tagId).asLiveData()

    fun insertTag(name: String) = viewModelScope.launch {
        repository.insertTag(TagEntity(name = name))
    }

    fun insertTodo(title: String, desc: String?, tagId: Int?, dueDate: Long?, isCompleted: Boolean) = viewModelScope.launch {
        repository.insertTodo(TodoEntity(title = title, description = desc, tagId = tagId, dueDateMillis = dueDate, isCompleted = isCompleted))
    }

    suspend fun insertTagReturnId(name: String): Int {
        return repository.insertTagReturnId(name)
    }

    fun deleteTag(tag: TagEntity) = viewModelScope.launch {
        repository.deleteTag(tag)
    }

    fun deleteTodo(todo: TodoEntity) = viewModelScope.launch {
        repository.deleteTodo(todo)
    }

    fun completeTodo(id: Int, isDone: Boolean) = viewModelScope.launch {
        repository.updateIsComplete(id, !isDone)
    }

    fun updateTodo(todo: TodoEntity) = viewModelScope.launch {
        repository.updateTodo(todo)
    }

    fun updateTag(tag: TagEntity) = viewModelScope.launch {
        repository.updateTag(tag)
    }
}
