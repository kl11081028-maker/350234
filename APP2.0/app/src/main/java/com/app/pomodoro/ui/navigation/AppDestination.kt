package com.app.pomodoro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

sealed class AppDestination(val route: String) {
    object Timer : AppDestination("timer")
    object Tasks : AppDestination("tasks")
    object History : AppDestination("history")
    object Settings : AppDestination("settings")
}

data class AdditionalDestination(
    val route: String,
    val Content: @Composable (NavHostController) -> Unit
)

fun bottomBarDestinations(): List<AppDestination> = listOf(
    AppDestination.Timer,
    AppDestination.Tasks,
    AppDestination.History,
    AppDestination.Settings
)

fun appDestinations(): List<AdditionalDestination> = emptyList()

