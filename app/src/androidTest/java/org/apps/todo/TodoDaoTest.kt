package org.apps.todo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.apps.todo.data.local.AppDatabase
import org.apps.todo.data.local.TagDao
import org.apps.todo.data.local.TagEntity
import org.apps.todo.data.local.TodoDao
import org.apps.todo.data.local.TodoEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var todoDao: TodoDao
    private lateinit var tagDao: TagDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        todoDao = db.todoDao()
        tagDao = db.tagDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetTodo() = runTest {
        val tagId = tagDao.insertTag(TagEntity(name = "Testing")).toInt()
        val todo = TodoEntity(title = "Learn DAO", tagId = tagId)
        todoDao.insertTodo(todo)

        val result = todoDao.getAllTodos().first()
        assertEquals(1, result.size)
        assertEquals("Learn DAO", result[0].title)
    }
}
