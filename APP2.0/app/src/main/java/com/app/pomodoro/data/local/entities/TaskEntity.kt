package com.app.pomodoro.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val pomodorosCompleted: Int,
    val estimatedPomodoros: Int?,
    val completed: Boolean,
    val createdAt: Long,
    val completedAt: Long?
)

