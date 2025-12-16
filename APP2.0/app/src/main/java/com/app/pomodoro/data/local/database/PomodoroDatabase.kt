package com.app.pomodoro.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.pomodoro.data.local.dao.PomodoroDao
import com.app.pomodoro.data.local.dao.TaskDao
import com.app.pomodoro.data.local.entities.PomodoroSessionEntity
import com.app.pomodoro.data.local.entities.TaskEntity

@Database(
    entities = [PomodoroSessionEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun taskDao(): TaskDao
}

