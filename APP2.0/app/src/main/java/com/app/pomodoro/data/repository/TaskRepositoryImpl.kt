package com.app.pomodoro.data.repository

import com.app.pomodoro.data.local.dao.TaskDao
import com.app.pomodoro.data.local.entities.TaskEntity
import com.app.pomodoro.data.mapper.toDomain
import com.app.pomodoro.data.mapper.toEntity
import com.app.pomodoro.domain.model.Task
import com.app.pomodoro.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {
    override fun getTasks(): Flow<List<Task>> = dao.getTasks().map { it.map(TaskEntity::toDomain) }

    override suspend fun upsertTask(task: Task) {
        dao.upsert(task.toEntity())
    }

    override suspend fun deleteTask(id: Long) {
        dao.delete(id)
    }
}

