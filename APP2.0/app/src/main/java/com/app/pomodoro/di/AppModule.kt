package com.app.pomodoro.di

import android.content.Context
import androidx.room.Room
import com.app.pomodoro.data.local.dao.PomodoroDao
import com.app.pomodoro.data.local.dao.TaskDao
import com.app.pomodoro.data.local.database.PomodoroDatabase
import com.app.pomodoro.data.repository.PomodoroRepositoryImpl
import com.app.pomodoro.data.repository.PreferencesRepositoryImpl
import com.app.pomodoro.data.repository.TaskRepositoryImpl
import com.app.pomodoro.domain.repository.PomodoroRepository
import com.app.pomodoro.domain.repository.PreferencesRepository
import com.app.pomodoro.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PomodoroDatabase =
        Room.databaseBuilder(context, PomodoroDatabase::class.java, "pomodoro.db").build()

    @Provides
    fun providePomodoroDao(db: PomodoroDatabase): PomodoroDao = db.pomodoroDao()

    @Provides
    fun provideTaskDao(db: PomodoroDatabase): TaskDao = db.taskDao()

    @Provides
    @Singleton
    fun providePomodoroRepository(impl: PomodoroRepositoryImpl): PomodoroRepository = impl

    @Provides
    @Singleton
    fun provideTaskRepository(impl: TaskRepositoryImpl): TaskRepository = impl

    @Provides
    @Singleton
    fun providePreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository = impl
}

