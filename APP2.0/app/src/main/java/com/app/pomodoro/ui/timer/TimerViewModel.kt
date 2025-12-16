package com.app.pomodoro.ui.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pomodoro.domain.model.PomodoroSession
import com.app.pomodoro.domain.usecase.GetPreferredDurationsUseCase
import com.app.pomodoro.domain.usecase.InsertSessionUseCase
import com.app.pomodoro.service.TimerManager
import com.app.pomodoro.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val timerManager: TimerManager,
    private val insertSession: InsertSessionUseCase,
    private val getDurations: GetPreferredDurationsUseCase
) : ViewModel() {

    private var lastDuration: Long = 0L
    private var lastSessionType: SessionType = SessionType.WORK

    val timerState: StateFlow<TimerState> = timerManager.state
        .stateIn(viewModelScope, SharingStarted.Eagerly, TimerState.Idle)

    val timeRemaining: StateFlow<Long> = timerManager.remaining
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    val progress: StateFlow<Float> = combine(timeRemaining, timerState) { time, state ->
        val total = when (state) {
            is TimerState.Running -> state.totalDuration
            else -> getDurations().workMillis
        }
        if (total > 0) time.toFloat() / total else 0f
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    init {
        viewModelScope.launch {
            timerManager.state.collect { state ->
                if (state is TimerState.Completed) {
                    onComplete(state.sessionType, lastDuration.takeIf { it > 0 } ?: getDurationForType(state.sessionType))
                }
            }
        }
    }

    fun start(sessionType: SessionType = SessionType.WORK) {
        viewModelScope.launch {
            val duration = getDurationForType(sessionType)
            lastDuration = duration
            lastSessionType = sessionType
            TimerService.startCommand(context, duration, sessionType)
        }
    }

    fun pause() {
        TimerService.pauseCommand(context)
    }

    fun resume() {
        viewModelScope.launch {
            val duration = getDurationForType(lastSessionType)
            lastDuration = duration
            TimerService.resumeCommand(context)
        }
    }

    fun reset() {
        TimerService.stopCommand(context)
    }

    private suspend fun onComplete(sessionType: SessionType, duration: Long) {
        insertSession(
            PomodoroSession(
                id = 0,
                taskId = null,
                startTime = System.currentTimeMillis() - duration,
                endTime = System.currentTimeMillis(),
                duration = (duration / 60000).toInt(),
                type = sessionType,
                completed = true
            )
        )
    }

    private suspend fun getDurationForType(sessionType: SessionType): Long {
        val prefs = getDurations()
        return when (sessionType) {
            SessionType.WORK -> prefs.workMillis
            SessionType.SHORT_BREAK -> prefs.shortBreakMillis
            SessionType.LONG_BREAK -> prefs.longBreakMillis
        }
    }
}

