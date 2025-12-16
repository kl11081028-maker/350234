package com.app.pomodoro.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.pomodoro.ui.timer.SessionType

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long?,
    val startTime: Long,
    val endTime: Long,
    val duration: Int,
    val type: SessionType,
    val completed: Boolean
)

