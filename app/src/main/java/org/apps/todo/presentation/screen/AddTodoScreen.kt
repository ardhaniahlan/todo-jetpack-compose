package org.apps.todo.presentation.screen

import android.app.Application
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.apps.todo.core.Constants.formatDate
import org.apps.todo.core.Constants.scheduleNotification
import org.apps.todo.presentation.viewmodel.TodoViewModel
import org.apps.todo.presentation.viewmodel.TodoViewModelFactory
import org.apps.todo.ui.theme.TodoTheme
import java.util.Calendar

@Composable
fun AddTodoScreen(
    todoId: Int? = null,
    navController: NavController,
    viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(LocalContext.current.applicationContext as Application
        )
    )
) {
    val tags by viewModel.allTags.observeAsState(emptyList())
    val todo by todoId?.let { viewModel.getTodosById(it).observeAsState() } ?: remember { mutableStateOf(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDateMillis by remember { mutableStateOf<Long?>(null) }
    var isCompleted by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf("") }

    LaunchedEffect(todo) {
        todo?.let {
            title = it.title
            description = it.description.orEmpty()
            dueDateMillis = it.dueDateMillis
            isCompleted = it.isCompleted
            selectedTag = tags.find { tag -> tag.id == it.tagId }?.name.orEmpty()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        AddTodoScreenContent(
            modifier = Modifier.padding(padding),
            title = title,
            description = description,
            selectedTag = selectedTag,
            dueDateMillis = dueDateMillis,
            tags = tags.map { it.name },
            onTitleChange = { title = it },
            onDescriptionChange = { description = it },
            onDueDateChange = { dueDateMillis = it },
            onTagSelect = { selectedTag = it },
            onSave = {
                coroutineScope.launch {
                    var tagId: Int? = null

                    // Cek / buat Tag kalau ada input
                    if (selectedTag.isNotBlank()) {
                        val existingTag = tags.find { it.name.equals(selectedTag, ignoreCase = true) }
                        tagId = existingTag?.id ?: viewModel.insertTagReturnId(selectedTag)
                    }

                    if (title.isNotBlank()) {
                        // ====== INSERT ======
                        if (todoId == null) {
                            viewModel.insertTodo(
                                title = title,
                                desc = description.takeIf { it.isNotBlank() },
                                tagId = tagId,
                                dueDate = dueDateMillis,
                                isCompleted = isCompleted
                            )
                        } else {
                            // ====== UPDATE ======
                            todo?.let {
                                viewModel.updateTodo(
                                    it.copy(
                                        title = title,
                                        description = description,
                                        tagId = tagId,
                                        dueDateMillis = dueDateMillis,
                                        isCompleted = isCompleted
                                    )
                                )
                            }
                        }

                        // Schedule notifikasi kalau ada dueDate
                        dueDateMillis?.let { triggerAt ->
                            scheduleNotification(
                                context = context,
                                todoId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), // id unik
                                title = title,
                                desc = description,
                                triggerAtMillis = triggerAt
                            )
                        }

                        launch{
                            snackbarHostState.showSnackbar(
                                if (todoId == null) "Todo berhasil ditambahkan"
                                else "Todo berhasil diperbarui"
                            )
                        }
                        navController.popBackStack()

                    } else if (selectedTag.isNotBlank()) {
                        // Hanya buat Tag baru
                        launch {
                            snackbarHostState.showSnackbar("Tag baru berhasil dibuat")
                        }
                        navController.popBackStack()

                    } else {
                        // Kosong semua
                        launch {

                            snackbarHostState.showSnackbar("Isi minimal judul atau tag")
                        }
                    }
                }

            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreenContent(
    modifier: Modifier,
    title: String,
    description: String,
    dueDateMillis: Long?,
    selectedTag: String,
    tags: List<String>,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTagSelect: (String) -> Unit,
    onDueDateChange: (Long) -> Unit,
    onSave: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add To-Do",
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Judul") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("TitleField"),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Deskripsi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
                    .testTag("DescriptionField")
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showDatePicker = true }.padding(start = 16.dp)
                    .testTag("DueDateRow")

            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Due Date")
                Text(
                    text = dueDateMillis?.let { formatDate(it)
                    } ?: "Due date",
                    modifier
                        .padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedTag,
                    onValueChange = { newValue ->
                        val sanitized = newValue.replace(" ", "")
                        onTagSelect(sanitized)
                    },
                    label = { Text("Pilih Tag") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("TagField"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )

                val filteredTags = tags.filter {
                    it.contains(selectedTag, ignoreCase = true)
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filteredTags.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = {
                                onTagSelect(tag)
                                expanded = false
                            },
                            modifier = Modifier.testTag("TagItem_$tag")
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("SaveButton")
            ) {
                Text("Simpan")
            }
        }

        // Due Date
        if (showDatePicker) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                LocalContext.current,
                { _, year, month, dayOfMonth ->
                    val cal = Calendar.getInstance()
                    cal.set(year, month, dayOfMonth, 0, 0, 0)
                    onDueDateChange(cal.timeInMillis)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddTodoScreenPreview() {
    TodoTheme {
        AddTodoScreenContent(
            modifier = Modifier,
            title = "Contoh Judul",
            description = "Deskripsi Todo",
            selectedTag = "Work",
            tags = listOf("Work", "Study", "Personal"),
            onTitleChange = {},
            onDescriptionChange = {},
            onTagSelect = {},
            onSave = {},
            onDueDateChange = {},
            dueDateMillis = null
        )
    }
}