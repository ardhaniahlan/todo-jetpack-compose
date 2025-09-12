package org.apps.todo

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import org.apps.todo.presentation.navigation.Screens
import org.apps.todo.presentation.screen.AddTodoScreen
import org.apps.todo.presentation.screen.HomeScreen
import org.apps.todo.presentation.screen.SearchScreen
import org.apps.todo.presentation.screen.SettingScreen
import org.apps.todo.presentation.screen.SplashScreen
import org.apps.todo.presentation.screen.TagDetailScreen
import org.apps.todo.presentation.screen.TodoDetailScreen
import org.apps.todo.presentation.viewmodel.ThemeViewModel
import org.apps.todo.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val themeViewModel: ThemeViewModel =
            ViewModelProvider(this)[ThemeViewModel::class.java]

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            var showSplash by remember { mutableStateOf(true) }
            val systemUiController = rememberSystemUiController()

            val statusBarColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.background
            val navigationBarColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.background

            val useDarkIcons = !isDarkTheme

            SideEffect {
                systemUiController.setStatusBarColor(
                    color = statusBarColor,
                    darkIcons = useDarkIcons
                )
                systemUiController.setNavigationBarColor(
                    color = navigationBarColor,
                    darkIcons = useDarkIcons
                )
            }

            LaunchedEffect(Unit) {
                delay(2000)
                showSplash = false
            }

            TodoTheme (darkTheme = isDarkTheme) {
                if (showSplash){
                    SplashScreen()
                } else {
                    MainScreen(themeViewModel)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(POST_NOTIFICATIONS),
                        1001
                    )
                }
            }

        }
    }
}

@Composable
fun MainScreen(themeViewModel: ThemeViewModel? = null) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            MyBottomAppBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.HomeScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.HomeScreen.route) { HomeScreen(navController = navController) }
            composable(Screens.TagDetailScreen.route) { backStackEntry ->
                val tagId = backStackEntry.arguments?.getString("tagId")?.toIntOrNull()
                tagId?.let {
                    TagDetailScreen(it, navController = navController)
                }
            }
            composable(Screens.TodoDetailScreen.route) { backStackEntry ->
                val todoId = backStackEntry.arguments?.getString("todoId")?.toInt() ?: 0
                TodoDetailScreen(
                    todoId,
                    navController = navController
                )
            }
            composable(
                route = Screens.AddTodoScreen.route,
                arguments = listOf(navArgument("todoId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val todoId = backStackEntry.arguments?.getInt("todoId")?.takeIf { it != -1 }
                AddTodoScreen(navController = navController, todoId = todoId)
            }

            composable(Screens.SearchScreen.route) { SearchScreen(navController = navController) }
            composable(Screens.SettingScreen.route) {
                themeViewModel?.let {
                    SettingScreen(it)
                }
            }
        }
    }
}

@Composable
fun MyBottomAppBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    // Tinggi responsif dengan batas min & max
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val barHeight = (screenHeight * 0.1f).coerceIn(56.dp, 80.dp)

    // Pakai Triple: (route, icon, label)
    val items = listOf(
        Triple(Screens.HomeScreen.route, Icons.Default.Home, "Home"),
        Triple(Screens.AddTodoScreen.route, Icons.Default.Add, "Add"),
        Triple(Screens.SearchScreen.route, Icons.Default.Search, "Search"),
        Triple(Screens.SettingScreen.route, Icons.Default.Settings, "Settings")
    )

    NavigationBar(
        modifier = Modifier.height(barHeight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (route, icon, label) ->
                NavigationBarItem(
                    selected = currentDestination == route,
                    onClick = {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.tertiary
                    ),
                    alwaysShowLabel = true
                )
            }
        }
    }
}

@Preview
@Composable
fun MyBottomBarPreview(){
    TodoTheme {
        MainScreen()
    }
}
