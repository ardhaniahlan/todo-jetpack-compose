package com.example.todoapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.apps.todo.presentation.screen.AddTodoScreenContent

@RunWith(AndroidJUnit4::class)
class AddTodoScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testInputAndSave() {
        var saved = false
        composeTestRule.setContent {
            AddTodoScreenContent(
                modifier = Modifier,
                title = "",
                description = "",
                dueDateMillis = null,
                selectedTag = "",
                tags = listOf("Work", "Personal"),
                onTitleChange = {},
                onDescriptionChange = {},
                onTagSelect = {},
                onDueDateChange = {},
                onSave = { saved = true }
            )
        }

        // Isi Judul
        composeTestRule.onNodeWithTag("TitleField").performTextInput("Belajar Compose")

        // Isi Deskripsi
        composeTestRule.onNodeWithTag("DescriptionField").performTextInput("Latihan UI Test")

        // Klik tombol simpan
        composeTestRule.onNodeWithTag("SaveButton").performClick()

        assert(saved)
    }

    @Test
    fun testTagDropdownSelection() {
        var selected = ""
        composeTestRule.setContent {
            AddTodoScreenContent(
                modifier = Modifier,
                title = "",
                description = "",
                dueDateMillis = null,
                selectedTag = "",
                tags = listOf("Work", "Personal"),
                onTitleChange = {},
                onDescriptionChange = {},
                onTagSelect = { selected = it },
                onDueDateChange = {},
                onSave = {}
            )
        }

        // Klik field Tag
        composeTestRule.onNodeWithTag("TagField").performClick()

        // Pilih item "Personal"
        composeTestRule.onNodeWithTag("TagItem_Personal").performClick()

        assert(selected == "Personal")
    }
}
