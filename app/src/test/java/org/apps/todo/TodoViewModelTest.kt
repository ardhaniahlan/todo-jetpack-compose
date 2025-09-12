package org.apps.todo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.apps.todo.data.repository.ITodoRepository
import org.apps.todo.presentation.viewmodel.TodoViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TodoViewModelTest {
    private lateinit var viewModel: TodoViewModel
    private lateinit var fakeRepository: ITodoRepository

    @Before
    fun setup() {
        fakeRepository = FakeTodoRepository()
        viewModel = TodoViewModel(fakeRepository)
    }

    @Test
    fun addTodo_reflectsInLiveData(): Unit = runTest {
        viewModel.insertTodo("Belajar ViewModel Test", null, null, null, false)

        val todos = viewModel.allTodos.getOrAwaitValue()
        assertEquals(1, todos.size)
        assertEquals("Belajar ViewModel Test", todos[0].title)
    }
}

