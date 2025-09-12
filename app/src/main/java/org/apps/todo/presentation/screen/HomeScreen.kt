package org.apps.todo.presentation.screen

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.apps.todo.core.Constants.formatDate
import org.apps.todo.presentation.navigation.Screens
import org.apps.todo.presentation.viewmodel.TodoViewModel
import org.apps.todo.presentation.viewmodel.TodoViewModelFactory
import org.apps.todo.data.local.TagEntity
import org.apps.todo.data.local.TodoEntity
import org.apps.todo.ui.theme.TodoTheme

@Composable
fun HomeScreen(
    navController: NavController,
){
    val context = LocalContext.current
    val viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(context.applicationContext as Application)
    )
    val todos by viewModel.allTodos.observeAsState(emptyList())
    val tags by viewModel.allTags.observeAsState(emptyList())

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        HomeScreenContent(
            modifier = Modifier.padding(innerPadding),
            todos = todos,
            onTodoClick = { todoid ->
                navController.navigate(Screens.TodoDetailScreen.createRoute(todoid))
            },
            tags = tags,
            onTagClick = { tagId ->
                navController.navigate(Screens.TagDetailScreen.createRoute(tagId))
            },
            onDeleteTagClick = { tag ->
                viewModel.deleteTag(tag)

                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Tag #${tag.name} Dihapus",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed){
                        viewModel.insertTag(tag.name)
                    }
                }
            },
            onInsertTagClick = { name ->
                viewModel.insertTag(name)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Tag #$name ditambahkan",
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onCheckCompleted = { id, isCompleted ->
                viewModel.completeTodo(id, isCompleted)
            },
            onUpdateTagClick = { tag, newName ->
                viewModel.updateTag(tag.copy(name = newName))
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Tag #${tag.name} diperbarui menjadi #$newName",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier,
    todos: List<TodoEntity>,
    onTodoClick: (Int) -> Unit,
    tags: List<TagEntity>,
    onTagClick: (Int) -> Unit,
    onDeleteTagClick: (TagEntity) -> Unit,
    onInsertTagClick: (String) -> Unit,
    onCheckCompleted: (Int, Boolean) -> Unit,
    onUpdateTagClick: (TagEntity, String) -> Unit

) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // State untuk mengontrol visibilitas dialog
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<TagEntity?>(null) }
    var showEditDialog by remember { mutableStateOf<TagEntity?>(null) }
    var showOptionsDialog by remember { mutableStateOf<TagEntity?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    text = "To-Do List",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // To-Do list, dibatasi tinggi max 50% layar
            item {
                if (todos.isEmpty()){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(screenHeight * 0.3f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada tugas yang tersedia.",
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.5f)
                            .padding(vertical = 8.dp),
                        userScrollEnabled = true
                    ) {
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

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tags",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(16.dp, 8.dp)
                            .weight(1f),
                    )
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Tag")
                    }
                }
            }

            item {
                if (tags.isEmpty()){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada tagar yang tersedia.",
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                tonalElevation = 2.dp,
                                modifier = Modifier.combinedClickable(
                                    onClick = { onTagClick(tag.id) },
                                    onLongClick = { showOptionsDialog = tag }
                                )
                            ) {
                                Text(
                                    text = "#${tag.name}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        showOptionsDialog?.let { tag ->
            AlertDialog(
                onDismissRequest = { showOptionsDialog = null },
                title = { Text("Tag Options") },
                text = { Text("Apa yang ingin dilakukan untuk #${tag.name}?") },
                // Custom button layout
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Tombol kiri
                        TextButton(onClick = {
                            showDeleteDialog = tag
                            showOptionsDialog = null
                        }) {
                            Text("Hapus", color = Color.Red)
                        }

                        // Tombol kanan (dibungkus Row kecil lagi biar rapat)
                        Row {
                            TextButton(onClick = { showOptionsDialog = null }) {
                                Text("Batal")
                            }
                            TextButton(onClick = {
                                showEditDialog = tag
                                showOptionsDialog = null
                            }) {
                                Text("Edit")
                            }
                        }
                    }
                }
            )
        }

        // Dialog Delete
        showDeleteDialog?.let{ tag ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = tag },
                title = { Text("Hapus Tag") },
                text = { Text("Yakin ingin menghapus #${tag.name} dan semua To-Do didalamnya ikut terhapus?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteTagClick(tag)
                            showDeleteDialog = null
                        }
                    ) {
                        Text("Hapus", color= Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Dialog Edit Tag
        showEditDialog?.let { tag ->
            var newName by remember { mutableStateOf(tag.name) }
            AlertDialog(
                onDismissRequest = { showEditDialog = null },
                title = { Text("Edit Tag") },
                text = {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it.replace(" ", "") },
                        label = { Text("Nama Tagar Baru") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newName.isNotBlank()) {
                            onUpdateTagClick(tag, newName)
                            showEditDialog = null
                        }
                    }) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = null }) {
                        Text("Batal")
                    }
                }
            )
        }


        // Dialog tambah tag
        if (showAddDialog) {
            var newTagName by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Tambah Tagar Baru") },
                text = {
                    TextField(
                        value = newTagName,
                        onValueChange = { input ->
                            // Hilangkan spasi
                            newTagName = input.replace(" ", "")
                            newTagName = input.filter { it.isLetterOrDigit() }
                        },
                        label = { Text("Nama Tagar") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )

                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newTagName.isNotBlank()) {
                            onInsertTagClick(newTagName)
                            newTagName = ""
                            showAddDialog = false
                        }
                    }) {
                        Text("Tambahkan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenContentPreview() {
    TodoTheme {
        val dummyTodos = listOf(
            TodoEntity(
                id = 1,
                title = "Belajar Jetpack Compose",
                description = "Latihan membuat UI",
                tagId = 1,
                dueDateMillis = System.currentTimeMillis() + 86400000, // besok
                isCompleted = false
            ),
            TodoEntity(
                id = 2,
                title = "Kerjain Skripsi",
                description = "Fokus bab 3 dan coding",
                tagId = 1,
                dueDateMillis = System.currentTimeMillis() + 2 * 86400000, // 2 hari lagi
                isCompleted = false
            ),
            TodoEntity(
                id = 3,
                title = "Olahraga",
                description = "Jogging 30 menit",
                tagId = 2,
                dueDateMillis = null, // tidak ada deadline
                isCompleted = true
            )
        )


        HomeScreenContent(
            modifier = Modifier,
            todos = dummyTodos,
            tags = listOf(
                TagEntity(1, "Kuliah"),
                TagEntity(2, "Rumah"),
                TagEntity(3, "Belanja")
            ),
            onTodoClick = {},
            onTagClick = {},
            onDeleteTagClick = {},
            onInsertTagClick = {},
            onCheckCompleted = { _, _ -> },
            onUpdateTagClick = { _, _ ->}
        )
    }
}