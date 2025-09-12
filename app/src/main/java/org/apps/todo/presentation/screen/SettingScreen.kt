package org.apps.todo.presentation.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.apps.todo.presentation.viewmodel.ThemeViewModel
import org.apps.todo.ui.theme.TodoTheme

@Composable
fun SettingScreen(themeViewModel: ThemeViewModel){
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    SettingScreenContent(
        isDarkTheme = isDarkTheme,
        onThemeChange = { themeViewModel.toggleTheme(it) }
    )
}

@Composable
fun SettingScreenContent(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (isDarkTheme) "Dark Mode" else "Light Mode",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = onThemeChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    TodoTheme {
        SettingScreenContent(
            isDarkTheme = true,
            onThemeChange = {}
        )
    }
}

