package com.app.pomodoro.domain.usecase

import com.app.pomodoro.domain.model.PreferredDurations
import com.app.pomodoro.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetPreferredDurationsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(): PreferredDurations = preferencesRepository.getPreferredDurations()
}

