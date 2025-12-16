package com.app.pomodoro.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.pomodoro.ui.navigation.AppDestination
import com.app.pomodoro.R

@Composable
fun AppScaffold(
    navController: NavHostController,
    bottomDestinations: List<AppDestination>,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomDestinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            when (destination) {
                                AppDestination.Timer -> Icon(Icons.Default.Timer, contentDescription = null)
                                AppDestination.Tasks -> Icon(Icons.Default.Task, contentDescription = null)
                                AppDestination.History -> Icon(Icons.Default.History, contentDescription = null)
                                AppDestination.Settings -> Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        },
                        label = {
                            val text = when (destination) {
                                AppDestination.Timer -> stringResource(R.string.nav_timer)
                                AppDestination.Tasks -> stringResource(R.string.nav_tasks)
                                AppDestination.History -> stringResource(R.string.nav_history)
                                AppDestination.Settings -> stringResource(R.string.nav_settings)
                            }
                            Text(text)
                        }
                    )
                }
            }
        },
        content = { padding -> content(padding) }
    )
}

