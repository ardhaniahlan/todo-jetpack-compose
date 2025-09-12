package org.apps.todo.presentation.screen

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.apps.todo.presentation.navigation.Screens
import org.apps.todo.presentation.viewmodel.TodoViewModel
import org.apps.todo.presentation.viewmodel.TodoViewModelFactory
import org.apps.todo.data.local.TodoEntity
import org.apps.todo.ui.theme.TodoTheme

@Composable
fun TodoDetailScreen(
    todoId: Int,
    navController: NavController,
    viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val todo by viewModel.getTodosById(todoId).observeAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    TodoDetailScreenContent(
        todo = todo,
        onDelete = {
            todo?.let {
                showDeleteDialog = true
            }
        },
        onBack = {
            navController.popBackStack()
        },
        onEdit = {
            navController.navigate(Screens.AddTodoScreen.createRoute(todoId))
        }
    )

    if (showDeleteDialog && todo != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus To-Do") },
            text = { Text("Apakah kamu yakin ingin menghapus todo \"${todo!!.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val backup = todo!!

                        viewModel.deleteTodo(backup)
                        showDeleteDialog = false
                        navController.popBackStack()

                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Hapus ${backup.title}",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.insertTodo(
                                    title = backup.title,
                                    desc = backup.description,
                                    tagId = backup.tagId,
                                    dueDate = backup.dueDateMillis,
                                    isCompleted = backup.isCompleted
                                )
                            }
                        }
                    }
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreenContent(
    todo: TodoEntity?,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onBack: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail To-Do", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    if (todo != null) {
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus") },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (todo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Memuat...")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                todo.description?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoDetailScreenPreview() {
    TodoTheme {
        // contoh data dummy untuk preview
        val dummyTodo = TodoEntity(
            id = 1,
            title = "Belajar Jetpack Compose",
            description = "Pelajari cara membuat UI dengan Compose",
            tagId = null
        )
        TodoDetailScreenContent(todo = dummyTodo, onDelete =  {}, onBack = {}, onEdit = {})
    }
}


