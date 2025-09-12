package org.apps.todo.presentation.screen

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.apps.todo.core.Constants.formatDate
import org.apps.todo.presentation.navigation.Screens
import org.apps.todo.presentation.viewmodel.TodoViewModel
import org.apps.todo.presentation.viewmodel.TodoViewModelFactory
import org.apps.todo.data.local.TagEntity
import org.apps.todo.data.local.TodoEntity

@Composable
fun TagDetailScreen(
    tagId: Int,
    navController: NavController
){
    val context = LocalContext.current
    val viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(context.applicationContext as Application)
    )

    val todos by viewModel.getTodosByTag(tagId).observeAsState(emptyList())
    val tags by viewModel.getTagById(tagId).observeAsState()

    TagDetailScreenContent(
        todos = todos,
        tag = tags,
        onTodoClick = { todoid ->
            navController.navigate(Screens.TodoDetailScreen.createRoute(todoid))
        },
        onBack = {
            navController.popBackStack()
        },
        onCheckCompleted = { id, isCompleted ->
            viewModel.completeTodo(id, isCompleted)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDetailScreenContent(
    todos: List<TodoEntity>,
    tag: TagEntity?,
    onTodoClick: (Int) -> Unit,
    onCheckCompleted: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text( "#${tag?.name}", fontWeight = FontWeight.Bold ) },
                navigationIcon = {
                    IconButton(onClick =  onBack ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (todos.isEmpty()) {
                Text(
                    text = "Belum ada todo untuk tag ini.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(todos) { todo ->
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTodoClick(todo.id) }
                                .padding(0.dp, 8.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = todo.isCompleted,
                                onCheckedChange = {
                                    onCheckCompleted(todo.id, todo.isCompleted)
                                }
                            )

                            val isOverdue = todo.dueDateMillis?.let {
                                it < System.currentTimeMillis() && !todo.isCompleted
                            } ?: false

                            Text(
                                text = todo.title,
                                fontWeight = FontWeight.Medium,
                                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f),
                                color = if (isOverdue) Color.Red else Color.Unspecified
                            )

                            todo.dueDateMillis?.let {
                                Text(
                                    text = formatDate(it),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isOverdue) Color.Red else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}