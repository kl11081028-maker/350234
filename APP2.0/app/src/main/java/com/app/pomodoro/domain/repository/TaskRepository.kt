package com.app.pomodoro.domain.repository

import com.app.pomodoro.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun upsertTask(task: Task)
    suspend fun deleteTask(id: Long)
}

