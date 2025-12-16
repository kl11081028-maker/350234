package com.app.pomodoro.domain.repository

import com.app.pomodoro.domain.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    fun getAllSessions(): Flow<List<PomodoroSession>>
    suspend fun insertSession(session: PomodoroSession)
}

