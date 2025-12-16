package com.app.pomodoro.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val pomodorosCompleted: Int = 0,
    val estimatedPomodoros: Int? = null,
    val completed: Boolean = false,
    val createdAt: Long,
    val completedAt: Long? = null
)

