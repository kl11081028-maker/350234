package com.app.pomodoro.service

import com.app.pomodoro.ui.timer.SessionType
import com.app.pomodoro.ui.timer.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 统一的计时管理器，供前台服务与 ViewModel 共享，保持状态与剩余时间。
 */
@Singleton
class TimerManager @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null
    private var pausedRemaining: Long? = null
    private var currentSessionType: SessionType = SessionType.WORK
    private var totalDuration: Long = 0L

    private val _state = MutableStateFlow<TimerState>(TimerState.Idle)
    val state: StateFlow<TimerState> = _state

    private val _remaining = MutableStateFlow(0L)
    val remaining: StateFlow<Long> = _remaining

    fun start(
        durationMillis: Long,
        sessionType: SessionType,
        fullDurationMillis: Long = durationMillis,
        onComplete: (() -> Unit)? = null,
        onTick: ((Long) -> Unit)? = null
    ) {
        stop()
        totalDuration = fullDurationMillis
        currentSessionType = sessionType
        _remaining.value = durationMillis
        _state.value = TimerState.Running(sessionType, fullDurationMillis)
        job = scope.launch {
            var remaining = durationMillis
            while (remaining >= 0) {
                _remaining.value = remaining
                onTick?.invoke(remaining)
                if (remaining == 0L) break
                delay(1000L)
                remaining -= 1000L
            }
            _state.value = TimerState.Completed(sessionType)
            pausedRemaining = null
            onComplete?.invoke()
        }
    }

    fun pause() {
        if (_state.value is TimerState.Running) {
            pausedRemaining = _remaining.value
            job?.cancel()
            _state.value = TimerState.Paused
        }
    }

    fun resume(onComplete: (() -> Unit)? = null, onTick: ((Long) -> Unit)? = null) {
        val remaining = pausedRemaining ?: return
        _state.value = TimerState.Running(currentSessionType, totalDuration)
        start(
            durationMillis = remaining,
            sessionType = currentSessionType,
            fullDurationMillis = totalDuration,
            onComplete = onComplete,
            onTick = onTick
        )
    }

    fun stop() {
        pausedRemaining = null
        job?.cancel()
        _state.value = TimerState.Idle
        _remaining.value = 0L
    }
}

