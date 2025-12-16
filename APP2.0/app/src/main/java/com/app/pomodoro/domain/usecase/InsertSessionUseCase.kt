package com.app.pomodoro.domain.usecase

import com.app.pomodoro.domain.model.PomodoroSession
import com.app.pomodoro.domain.repository.PomodoroRepository
import javax.inject.Inject

class InsertSessionUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    suspend operator fun invoke(session: PomodoroSession) = repository.insertSession(session)
}

