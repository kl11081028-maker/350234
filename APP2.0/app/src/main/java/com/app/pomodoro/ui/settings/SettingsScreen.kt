package com.app.pomodoro.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.pomodoro.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.nav_settings))
        Text("${stringResource(R.string.nav_timer)}: ${preferences.pomodoroDuration} min")
        Text("${stringResource(R.string.label_short_break)}: ${preferences.shortBreakDuration} min")
        Text("${stringResource(R.string.label_long_break)}: ${preferences.longBreakDuration} min")
    }
}

