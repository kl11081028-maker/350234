package com.app.pomodoro.data.mapper

import com.app.pomodoro.data.local.entities.PomodoroSessionEntity
import com.app.pomodoro.data.local.entities.TaskEntity
import com.app.pomodoro.domain.model.PomodoroSession
import com.app.pomodoro.domain.model.Task

fun PomodoroSessionEntity.toDomain() = PomodoroSession(
    id = id,
    taskId = taskId,
    startTime = startTime,
    endTime = endTime,
    duration = duration,
    type = type,
    completed = completed
)

fun PomodoroSession.toEntity() = PomodoroSessionEntity(
    id = id,
    taskId = taskId,
    startTime = startTime,
    endTime = endTime,
    duration = duration,
    type = type,
    completed = completed
)

fun TaskEntity.toDomain() = Task(
    id = id,
    title = title,
    description = description,
    pomodorosCompleted = pomodorosCompleted,
    estimatedPomodoros = estimatedPomodoros,
    completed = completed,
    createdAt = createdAt,
    completedAt = completedAt
)

fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    pomodorosCompleted = pomodorosCompleted,
    estimatedPomodoros = estimatedPomodoros,
    completed = completed,
    createdAt = createdAt,
    completedAt = completedAt
)

