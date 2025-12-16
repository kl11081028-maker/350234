package com.app.pomodoro.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pomodoro.domain.model.UserPreferences
import com.app.pomodoro.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: PreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = repository.preferencesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, UserPreferences())

    suspend fun update(block: (UserPreferences) -> UserPreferences) {
        repository.update(block)
    }
}

