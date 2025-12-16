package com.app.pomodoro.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.pomodoro.domain.model.AppTheme
import com.app.pomodoro.domain.model.PreferredDurations
import com.app.pomodoro.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

private const val DATASTORE_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

@Singleton
class PreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val POMODORO = intPreferencesKey("pomodoro_duration")
        val SHORT_BREAK = intPreferencesKey("short_break_duration")
        val LONG_BREAK = intPreferencesKey("long_break_duration")
        val LONG_INTERVAL = intPreferencesKey("long_break_interval")
        val NOTIFICATION = booleanPreferencesKey("notifications_enabled")
        val SOUND = stringPreferencesKey("selected_sound_uri")
        val AUTO_BREAK = booleanPreferencesKey("auto_start_breaks")
        val AUTO_POMODORO = booleanPreferencesKey("auto_start_pomodoros")
        val THEME = stringPreferencesKey("theme")
        val LOCALE = stringPreferencesKey("locale")
    }

    val flow: Flow<UserPreferences> = context.dataStore.data.map { prefs -> prefs.toUserPreferences() }

    suspend fun update(block: (UserPreferences) -> UserPreferences) {
        context.dataStore.edit { prefs ->
            val current = prefs.toUserPreferences()
            val updated = block(current)
            prefs[Keys.POMODORO] = updated.pomodoroDuration
            prefs[Keys.SHORT_BREAK] = updated.shortBreakDuration
            prefs[Keys.LONG_BREAK] = updated.longBreakDuration
            prefs[Keys.LONG_INTERVAL] = updated.longBreakInterval
            prefs[Keys.NOTIFICATION] = updated.notificationsEnabled
            updated.selectedSoundUri?.let { prefs[Keys.SOUND] = it } ?: prefs.remove(Keys.SOUND)
            prefs[Keys.AUTO_BREAK] = updated.autoStartBreaks
            prefs[Keys.AUTO_POMODORO] = updated.autoStartPomodoros
            prefs[Keys.THEME] = updated.theme.name
            updated.locale?.let { prefs[Keys.LOCALE] = it } ?: prefs.remove(Keys.LOCALE)
        }
    }

    suspend fun getDurations(): PreferredDurations {
        val current = flow.firstOrNull() ?: UserPreferences()
        return PreferredDurations(
            workMillis = current.pomodoroDuration * 60_000L,
            shortBreakMillis = current.shortBreakDuration * 60_000L,
            longBreakMillis = current.longBreakDuration * 60_000L
        )
    }

    private fun Preferences.toUserPreferences(): UserPreferences {
        val themeName = this[Keys.THEME] ?: AppTheme.SYSTEM.name
        return UserPreferences(
            pomodoroDuration = this[Keys.POMODORO] ?: 25,
            shortBreakDuration = this[Keys.SHORT_BREAK] ?: 5,
            longBreakDuration = this[Keys.LONG_BREAK] ?: 15,
            longBreakInterval = this[Keys.LONG_INTERVAL] ?: 4,
            notificationsEnabled = this[Keys.NOTIFICATION] ?: true,
            selectedSoundUri = this[Keys.SOUND],
            autoStartBreaks = this[Keys.AUTO_BREAK] ?: false,
            autoStartPomodoros = this[Keys.AUTO_POMODORO] ?: false,
            theme = runCatching { AppTheme.valueOf(themeName) }.getOrElse { AppTheme.SYSTEM },
            locale = this[Keys.LOCALE]
        )
    }
}

