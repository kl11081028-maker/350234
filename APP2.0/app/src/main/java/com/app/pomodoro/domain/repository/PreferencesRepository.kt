package com.app.pomodoro.domain.repository

import com.app.pomodoro.domain.model.PreferredDurations
import com.app.pomodoro.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun preferencesFlow(): Flow<UserPreferences>
    suspend fun update(block: (UserPreferences) -> UserPreferences)
    suspend fun getPreferredDurations(): PreferredDurations
}

