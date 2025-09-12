package org.apps.todo.presentation.screen

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.apps.todo.presentation.navigation.Screens
import org.apps.todo.presentation.viewmodel.TodoViewModel
import org.apps.todo.presentation.viewmodel.TodoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController
){
    val viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val todos by viewModel.allTodos.observeAsState(emptyList())

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val filteredTodo = if (query.isEmpty()) todos
    else todos.filter { it.title.contains(query, ignoreCase = true) }

    DockedSearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        leadingIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        placeholder = {
            Text("Search To-do")
        },
        shape = RoundedCornerShape(16.dp),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxSize() // ⬅️ SearchBar isi seluruh screen
            .focusRequester(focusRequester) // ⬅️ agar bisa auto fokus
    ) {
        // hasil pencarian
        LazyColumn {
            items(filteredTodo) { todo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Screens.TodoDetailScreen.createRoute(todo.id))
                        }
                        .padding(8.dp),
                ) {
                    Column {
                        Text(text = todo.title, fontWeight = FontWeight.Medium)
                        todo.description?.let {
                            Text(text = it, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}