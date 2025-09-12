package org.apps.todo.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.apps.todo.data.local.AppDatabase
import org.apps.todo.data.repository.TodoRepositoryImpl

class TodoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            val db = AppDatabase.getInstance(application)
            val repository = TodoRepositoryImpl(db.tagDao(), db.todoDao())
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
