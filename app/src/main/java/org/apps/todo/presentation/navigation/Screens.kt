package org.apps.todo.presentation.navigation

sealed class Screens(val route: String) {
    object HomeScreen: Screens("home")
    object SearchScreen: Screens("search")
    object SettingScreen: Screens("setting")

    object AddTodoScreen: Screens("addEditTodo?todoId={todoId}"){
        fun createRoute(todoId: Int? = null): String {
            return if (todoId != null) "addEditTodo?todoId=$todoId"
            else "addEditTodo"
        }
    }

    object TagDetailScreen: Screens("tagDetail/{tagId}"){
        fun createRoute(tagId: Int) = "tagDetail/${tagId}"
    }
    object TodoDetailScreen : Screens("todoDetail/{todoId}") {
        fun createRoute(todoId: Int) = "todoDetail/$todoId"
    }
}