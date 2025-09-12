package org.apps.todo.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.apps.todo.ui.theme.ThemePreferences

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = ThemePreferences(application)

    val isDarkTheme = preferences.getTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferences.saveTheme(enabled)
        }
    }
}