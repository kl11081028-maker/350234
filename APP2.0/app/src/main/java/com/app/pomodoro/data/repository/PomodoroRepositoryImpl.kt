package com.app.pomodoro.data.repository

import com.app.pomodoro.data.local.dao.PomodoroDao
import com.app.pomodoro.data.mapper.toDomain
import com.app.pomodoro.data.mapper.toEntity
import com.app.pomodoro.domain.model.PomodoroSession
import com.app.pomodoro.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepositoryImpl @Inject constructor(
    private val dao: PomodoroDao
) : PomodoroRepository {
    override fun getAllSessions(): Flow<List<PomodoroSession>> =
        dao.getAllSessions().map { list -> list.map { it.toDomain() } }

    override suspend fun insertSession(session: PomodoroSession) {
        dao.insertSession(session.toEntity())
    }
}

