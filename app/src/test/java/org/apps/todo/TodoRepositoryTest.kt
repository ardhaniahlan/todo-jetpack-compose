package org.apps.todo

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.apps.todo.data.local.TodoEntity
import org.apps.todo.data.repository.ITodoRepository
import org.apps.todo.data.repository.TodoRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TodoRepositoryTest {
    private lateinit var repository: ITodoRepository

    @Before
    fun setup() {
        repository = TodoRepositoryImpl(FakeTagDao(), FakeTodoDao())
    }

    @Test
    fun insertTodo_updatesFlow(): Unit = runTest {
        repository.insertTodo(TodoEntity(title = "Unit Test Repo"))

        val todos = repository.getAllTodos().first()
        assertEquals(1, todos.size)
        assertEquals("Unit Test Repo", todos[0].title)
    }
}
