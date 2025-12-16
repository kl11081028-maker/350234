package com.app.pomodoro.ui.timer

sealed class TimerState {
    object Idle : TimerState()
    data class Running(val sessionType: SessionType, val totalDuration: Long) : TimerState()
    object Paused : TimerState()
    data class Completed(val sessionType: SessionType) : TimerState()
}

enum class SessionType { WORK, SHORT_BREAK, LONG_BREAK }

