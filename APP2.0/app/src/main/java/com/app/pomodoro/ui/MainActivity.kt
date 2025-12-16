package com.app.pomodoro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.pomodoro.ui.navigation.AppDestination
import com.app.pomodoro.ui.navigation.appDestinations
import com.app.pomodoro.ui.navigation.bottomBarDestinations
import com.app.pomodoro.ui.theme.PomodoroTheme
import com.app.pomodoro.ui.timer.TimerScreen
import com.app.pomodoro.ui.tasks.TasksScreen
import com.app.pomodoro.ui.history.HistoryScreen
import com.app.pomodoro.ui.settings.SettingsScreen
import com.app.pomodoro.ui.components.AppScaffold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroApp()
        }
    }
}

@Composable
private fun PomodoroApp() {
    PomodoroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    AppScaffold(
        navController = navController,
        bottomDestinations = bottomBarDestinations()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Timer.route
        ) {
            composable(AppDestination.Timer.route) {
                TimerScreen(
                    viewModel = hiltViewModel(),
                    navigateToHistory = { navController.navigate(AppDestination.History.route) }
                )
            }
            composable(AppDestination.Tasks.route) {
                TasksScreen(
                    viewModel = hiltViewModel()
                )
            }
            composable(AppDestination.History.route) {
                HistoryScreen(
                    viewModel = hiltViewModel()
                )
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(
                    viewModel = hiltViewModel()
                )
            }
            appDestinations().forEach { destination ->
                composable(destination.route) { destination.Content(navController) }
            }
        }
    }
}

