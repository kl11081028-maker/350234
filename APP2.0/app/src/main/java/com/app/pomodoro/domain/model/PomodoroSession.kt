package com.app.pomodoro.domain.model

import com.app.pomodoro.ui.timer.SessionType

data class PomodoroSession(
    val id: Long = 0,
    val taskId: Long?,
    val startTime: Long,
    val endTime: Long,
    val duration: Int, // minutes
    val type: SessionType,
    val completed: Boolean
)

