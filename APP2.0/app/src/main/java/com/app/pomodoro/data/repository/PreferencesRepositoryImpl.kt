package com.app.pomodoro.data.repository

import com.app.pomodoro.data.preferences.PreferencesDataSource
import com.app.pomodoro.domain.model.PreferredDurations
import com.app.pomodoro.domain.model.UserPreferences
import com.app.pomodoro.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataSource: PreferencesDataSource
) : PreferencesRepository {
    override fun preferencesFlow(): Flow<UserPreferences> = dataSource.flow

    override suspend fun update(block: (UserPreferences) -> UserPreferences) {
        dataSource.update(block)
    }

    override suspend fun getPreferredDurations(): PreferredDurations = dataSource.getDurations()
}

