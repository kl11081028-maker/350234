package com.app.pomodoro.domain.model

enum class AppTheme { LIGHT, DARK, SYSTEM }

data class UserPreferences(
    val pomodoroDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val longBreakInterval: Int = 4,
    val notificationsEnabled: Boolean = true,
    val selectedSoundUri: String? = null,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
    val locale: String? = null
)

data class PreferredDurations(
    val workMillis: Long,
    val shortBreakMillis: Long,
    val longBreakMillis: Long
)

