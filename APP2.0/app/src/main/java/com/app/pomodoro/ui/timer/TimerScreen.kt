package com.app.pomodoro.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.pomodoro.R

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    navigateToHistory: () -> Unit
) {
    val state by viewModel.timerState.collectAsState()
    val remaining by viewModel.timeRemaining.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = formatTime(remaining),
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = when (state) {
                TimerState.Idle -> "· " + stringResource(R.string.state_idle)
                is TimerState.Running -> "· " + stringResource(R.string.state_running)
                TimerState.Paused -> "· " + stringResource(R.string.state_paused)
                is TimerState.Completed -> "· " + stringResource(R.string.state_completed)
            },
            style = MaterialTheme.typography.bodyLarge
        )

        SessionTypeSelector(
            onWork = { viewModel.start(SessionType.WORK) },
            onShort = { viewModel.start(SessionType.SHORT_BREAK) },
            onLong = { viewModel.start(SessionType.LONG_BREAK) }
        )

        ControlButtons(
            state = state,
            onStart = { viewModel.start() },
            onPause = { viewModel.pause() },
            onResume = { viewModel.resume() },
            onReset = { viewModel.reset() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = navigateToHistory, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.nav_history))
        }
    }
}

@Composable
private fun SessionTypeSelector(
    onWork: () -> Unit,
    onShort: () -> Unit,
    onLong: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ElevatedButton(onClick = onWork, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.label_work))
        }
        ElevatedButton(onClick = onShort, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.label_short_break))
        }
        ElevatedButton(onClick = onLong, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.label_long_break))
        }
    }
}

@Composable
private fun ControlButtons(
    state: TimerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (state) {
            is TimerState.Idle, is TimerState.Completed -> {
                Button(onClick = onStart, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.action_start))
                }
            }
            is TimerState.Running -> {
                Button(onClick = onPause, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.action_pause))
                }
            }
            is TimerState.Paused -> {
                Button(onClick = onResume, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.action_resume))
                }
            }
        }
        OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.action_reset))
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

