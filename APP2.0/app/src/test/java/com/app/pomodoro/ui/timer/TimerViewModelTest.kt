package com.app.pomodoro.ui.timer

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.pomodoro.domain.model.PreferredDurations
import com.app.pomodoro.domain.usecase.InsertSessionUseCase
import com.app.pomodoro.domain.usecase.GetPreferredDurationsUseCase
import com.app.pomodoro.service.TimerManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockTimerManager = mockk<TimerManager>()
    private val mockInsertSession = mockk<InsertSessionUseCase>()
    private val mockGetDurations = mockk<GetPreferredDurationsUseCase>()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: TimerViewModel

    @Before
    fun setUp() {
        // 模拟 TimerManager 的流
        every { mockTimerManager.state } returns MutableStateFlow(TimerState.Idle)
        every { mockTimerManager.remaining } returns MutableStateFlow(0L)

        // 模拟 GetPreferredDurationsUseCase 返回默认值
        coEvery { mockGetDurations() } returns PreferredDurations(
            workMillis = 25 * 60 * 1000L,
            shortBreakMillis = 5 * 60 * 1000L,
            longBreakMillis = 15 * 60 * 1000L
        )

        // 模拟 InsertSessionUseCase
        coEvery { mockInsertSession(any()) } just Runs

        // 静态模拟 TimerService
        mockkStatic("com.app.pomodoro.service.TimerService")
        every { com.app.pomodoro.service.TimerService.startCommand(any(), any(), any()) } just Runs
        every { com.app.pomodoro.service.TimerService.pauseCommand(any()) } just Runs
        every { com.app.pomodoro.service.TimerService.resumeCommand(any()) } just Runs
        every { com.app.pomodoro.service.TimerService.stopCommand(any()) } just Runs

        // 创建 ViewModel 实例
        viewModel = TimerViewModel(
            context = mockContext,
            timerManager = mockTimerManager,
            insertSession = mockInsertSession,
            getDurations = mockGetDurations
        )
    }

    @Test
    fun `test start method calls TimerService startCommand`() = testScope.runTest {
        // 执行测试
        viewModel.start(SessionType.WORK)

        // 验证结果
        verify {
            com.app.pomodoro.service.TimerService.startCommand(
                mockContext,
                25 * 60 * 1000L,
                SessionType.WORK
            )
        }
    }

    @Test
    fun `test pause method calls TimerService pauseCommand`() {
        // 执行测试
        viewModel.pause()

        // 验证结果
        verify {
            com.app.pomodoro.service.TimerService.pauseCommand(mockContext)
        }
    }

    @Test
    fun `test resume method calls TimerService resumeCommand`() = testScope.runTest {
        // 执行测试
        viewModel.resume()

        // 验证结果
        verify {
            com.app.pomodoro.service.TimerService.resumeCommand(mockContext)
        }
    }

    @Test
    fun `test reset method calls TimerService stopCommand`() {
        // 执行测试
        viewModel.reset()

        // 验证结果
        verify {
            com.app.pomodoro.service.TimerService.stopCommand(mockContext)
        }
    }

    @Test
    fun `test getDurationForType returns correct durations`() = testScope.runTest {
        // 准备测试数据
        val testCases = listOf(
            SessionType.WORK to 25 * 60 * 1000L,
            SessionType.SHORT_BREAK to 5 * 60 * 1000L,
            SessionType.LONG_BREAK to 15 * 60 * 1000L
        )

        // 模拟私有方法调用
        val privateMethod = TimerViewModel::class.java.getDeclaredMethod("getDurationForType", SessionType::class.java)
        privateMethod.isAccessible = true

        // 执行测试并验证结果
        for ((sessionType, expectedDuration) in testCases) {
            val result = privateMethod.invoke(viewModel, sessionType) as Long
            assert(result == expectedDuration) {
                "Expected duration for $sessionType: $expectedDuration, but got: $result"
            }
        }
    }

    @Test
    fun `test progress calculation when timer is running`() = testScope.runTest {
        // 模拟 TimerManager 的状态和剩余时间
        val mockStateFlow = MutableStateFlow(
            TimerState.Running(SessionType.WORK, 25 * 60 * 1000L)
        )
        val mockRemainingFlow = MutableStateFlow(12 * 60 * 1000L + 30 * 1000L) // 12分30秒

        every { mockTimerManager.state } returns mockStateFlow
        every { mockTimerManager.remaining } returns mockRemainingFlow

        // 创建新的 ViewModel 实例
        val viewModel = TimerViewModel(
            context = mockContext,
            timerManager = mockTimerManager,
            insertSession = mockInsertSession,
            getDurations = mockGetDurations
        )

        // 验证进度计算
        val expectedProgress = (12 * 60 * 1000L + 30 * 1000L).toFloat() / (25 * 60 * 1000L)
        assert(viewModel.progress.value == expectedProgress) {
            "Expected progress: $expectedProgress, but got: ${viewModel.progress.value}"
        }
    }

    @Test
    fun `test progress calculation when timer is idle`() = testScope.runTest {
        // 模拟 TimerManager 的状态和剩余时间
        val mockStateFlow = MutableStateFlow(TimerState.Idle)
        val mockRemainingFlow = MutableStateFlow(0L)

        every { mockTimerManager.state } returns mockStateFlow
        every { mockTimerManager.remaining } returns mockRemainingFlow

        // 创建新的 ViewModel 实例
        val viewModel = TimerViewModel(
            context = mockContext,
            timerManager = mockTimerManager,
            insertSession = mockInsertSession,
            getDurations = mockGetDurations
        )

        // 验证进度计算
        assert(viewModel.progress.value == 0f) {
            "Expected progress: 0f, but got: ${viewModel.progress.value}"
        }
    }
}
