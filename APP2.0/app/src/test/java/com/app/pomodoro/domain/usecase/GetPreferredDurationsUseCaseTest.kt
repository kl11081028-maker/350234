package com.app.pomodoro.domain.usecase

import com.app.pomodoro.domain.model.PreferredDurations
import com.app.pomodoro.domain.repository.PreferencesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPreferredDurationsUseCaseTest {

    private val mockPreferencesRepository = mockk<PreferencesRepository>()
    private lateinit var getPreferredDurationsUseCase: GetPreferredDurationsUseCase

    @Before
    fun setUp() {
        getPreferredDurationsUseCase = GetPreferredDurationsUseCase(mockPreferencesRepository)
    }

    @Test
    fun `test invoke returns preferred durations from repository`() = runTest {
        // 准备测试数据
        val expectedDurations = PreferredDurations(
            workMillis = 25 * 60 * 1000L,
            shortBreakMillis = 5 * 60 * 1000L,
            longBreakMillis = 15 * 60 * 1000L
        )

        // 模拟 PreferencesRepository 的行为
        coEvery { mockPreferencesRepository.getPreferredDurations() } returns expectedDurations

        // 执行测试
        val result = getPreferredDurationsUseCase()

        // 验证结果
        assert(result == expectedDurations) {
            "Expected $expectedDurations, but got $result"
        }
    }

    @Test
    fun `test invoke returns custom durations from repository`() = runTest {
        // 准备测试数据
        val expectedDurations = PreferredDurations(
            workMillis = 50 * 60 * 1000L,
            shortBreakMillis = 10 * 60 * 1000L,
            longBreakMillis = 30 * 60 * 1000L
        )

        // 模拟 PreferencesRepository 的行为
        coEvery { mockPreferencesRepository.getPreferredDurations() } returns expectedDurations

        // 执行测试
        val result = getPreferredDurationsUseCase()

        // 验证结果
        assert(result == expectedDurations) {
            "Expected $expectedDurations, but got $result"
        }
    }
}
