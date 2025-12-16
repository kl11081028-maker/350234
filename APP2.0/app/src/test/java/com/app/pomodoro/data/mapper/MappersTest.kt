package com.app.pomodoro.data.mapper

import com.app.pomodoro.data.local.entities.PomodoroSessionEntity
import com.app.pomodoro.data.local.entities.TaskEntity
import com.app.pomodoro.domain.model.PomodoroSession
import com.app.pomodoro.domain.model.Task
import com.app.pomodoro.ui.timer.SessionType
import org.junit.Test

class MappersTest {

    @Test
    fun `test PomodoroSessionEntity toDomain mapping`() {
        // 准备测试数据
        val entity = PomodoroSessionEntity(
            id = 1L,
            taskId = 10L,
            startTime = 1609459200000L, // 2021-01-01 00:00:00
            endTime = 1609460100000L,   // 2021-01-01 00:15:00
            duration = 15,
            type = SessionType.WORK,
            completed = true
        )

        // 执行映射
        val domain = entity.toDomain()

        // 验证结果
        assert(domain.id == entity.id)
        assert(domain.taskId == entity.taskId)
        assert(domain.startTime == entity.startTime)
        assert(domain.endTime == entity.endTime)
        assert(domain.duration == entity.duration)
        assert(domain.type == entity.type)
        assert(domain.completed == entity.completed)
    }

    @Test
    fun `test PomodoroSession toEntity mapping`() {
        // 准备测试数据
        val domain = PomodoroSession(
            id = 2L,
            taskId = 20L,
            startTime = 1609459200000L,
            endTime = 1609460100000L,
            duration = 15,
            type = SessionType.SHORT_BREAK,
            completed = false
        )

        // 执行映射
        val entity = domain.toEntity()

        // 验证结果
        assert(entity.id == domain.id)
        assert(entity.taskId == domain.taskId)
        assert(entity.startTime == domain.startTime)
        assert(entity.endTime == domain.endTime)
        assert(entity.duration == domain.duration)
        assert(entity.type == domain.type)
        assert(entity.completed == domain.completed)
    }

    @Test
    fun `test TaskEntity toDomain mapping`() {
        // 准备测试数据
        val entity = TaskEntity(
            id = 3L,
            title = "Test Task",
            description = "Test Description",
            pomodorosCompleted = 5,
            estimatedPomodoros = 10,
            completed = true,
            createdAt = 1609459200000L,
            completedAt = 1609460100000L
        )

        // 执行映射
        val domain = entity.toDomain()

        // 验证结果
        assert(domain.id == entity.id)
        assert(domain.title == entity.title)
        assert(domain.description == entity.description)
        assert(domain.pomodorosCompleted == entity.pomodorosCompleted)
        assert(domain.estimatedPomodoros == entity.estimatedPomodoros)
        assert(domain.completed == entity.completed)
        assert(domain.createdAt == entity.createdAt)
        assert(domain.completedAt == entity.completedAt)
    }

    @Test
    fun `test Task toEntity mapping`() {
        // 准备测试数据
        val domain = Task(
            id = 4L,
            title = "Another Task",
            description = null,
            pomodorosCompleted = 2,
            estimatedPomodoros = null,
            completed = false,
            createdAt = 1609459200000L,
            completedAt = null
        )

        // 执行映射
        val entity = domain.toEntity()

        // 验证结果
        assert(entity.id == domain.id)
        assert(entity.title == domain.title)
        assert(entity.description == domain.description)
        assert(entity.pomodorosCompleted == domain.pomodorosCompleted)
        assert(entity.estimatedPomodoros == domain.estimatedPomodoros)
        assert(entity.completed == domain.completed)
        assert(entity.createdAt == domain.createdAt)
        assert(entity.completedAt == domain.completedAt)
    }

    @Test
    fun `test bi-directional mapping for PomodoroSession`() {
        // 准备测试数据
        val originalEntity = PomodoroSessionEntity(
            id = 5L,
            taskId = 50L,
            startTime = 1609459200000L,
            endTime = 1609460100000L,
            duration = 15,
            type = SessionType.LONG_BREAK,
            completed = true
        )

        // 执行双向映射
        val domain = originalEntity.toDomain()
        val mappedEntity = domain.toEntity()

        // 验证结果
        assert(mappedEntity == originalEntity) {
            "Expected $originalEntity, but got $mappedEntity"
        }
    }

    @Test
    fun `test bi-directional mapping for Task`() {
        // 准备测试数据
        val originalEntity = TaskEntity(
            id = 6L,
            title = "Bi-directional Test",
            description = "Test bi-directional mapping",
            pomodorosCompleted = 3,
            estimatedPomodoros = 8,
            completed = false,
            createdAt = 1609459200000L,
            completedAt = null
        )

        // 执行双向映射
        val domain = originalEntity.toDomain()
        val mappedEntity = domain.toEntity()

        // 验证结果
        assert(mappedEntity == originalEntity) {
            "Expected $originalEntity, but got $mappedEntity"
        }
    }
}
