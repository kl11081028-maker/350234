# 番茄钟计时器应用程序设计文档

## 1. 执行概要
本文档概述使用 Kotlin 构建的原生 Android 番茄钟应用。目标是简洁、高性能、良好用户体验，并提供高效工具链与可迭代的功能演进路径。

## 2. 技术架构
### 2.1 技术栈
- 语言：Kotlin  
- 最低 SDK：API 24（Android 7.0）  
- 目标 SDK：API 34（Android 14）  
- 架构：MVVM  
- 关键库：Jetpack Compose、Room、WorkManager、Kotlin Coroutines、Hilt、DataStore

### 2.2 项目结构
```
com.app.pomodoro/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── entities/
│   │   └── database/
│   ├── repository/
│   └── preferences/
├── domain/
│   ├── model/
│   └── usecase/
├── ui/
│   ├── timer/
│   ├── tasks/
│   ├── history/
│   ├── settings/
│   └── components/
├── service/
└── utils/
```

## 3. 界面设计
### 3.1 主计时器屏幕
- 标题：显示/编辑当前任务
- 倒计时：番茄形状填充动画；大号时间显示（MM:SS）
- 控制：开始/暂停、重置按钮
- 会话指示：当前番茄钟计数与类型
- 快速统计：今日完成数量
- 视觉：主色番茄红 #FF6347，浅/深色背景，Roboto 字体，平滑过渡动画

### 3.2 任务管理屏幕
- 任务输入框 + 添加按钮
- 任务列表（RecyclerView/Compose 列表）带滑动手势
  - 右滑完成；左滑删除
  - 展示名称、番茄计数、完成状态
- 筛选：进行中/已完成标签页

### 3.3 历史记录屏幕
- 日期选择器
- 统计卡片：完成总数、总专注时长、最长连胜
- 时间线：完成会话列表及时间戳
- 图表：周/月进度可视化

### 3.4 设置屏幕
- 定时器：工作/短休/长休时长，长休间隔
- 通知：开关、声音选择、振动
- 外观：浅/深/系统主题，动画风格
- 高级：自动开始休息/番茄钟、会话期间保持屏幕常亮
- 数据：导出历史、清除历史、可选云同步

## 4. 功能逻辑
### 4.1 定时器状态机
- 状态：IDLE → RUNNING → PAUSED → COMPLETED → BREAK；任意状态可 Reset 到 IDLE
- 关键事件：开始、暂停、恢复、计时结束、进入休息、休息结束

### 4.2 通知流程
- 会话结束通知：提示“专注完成/休息结束”，动作：开始休息/跳过；高优先级、可选声音
- 后台常驻通知：显示剩余时间，动作：暂停/停止

### 4.3 数据模型
```kotlin
@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long?,
    val startTime: Long,
    val endTime: Long,
    val duration: Int, // 分钟
    val type: SessionType, // WORK, SHORT_BREAK, LONG_BREAK
    val completed: Boolean
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val pomodorosCompleted: Int = 0,
    val estimatedPomodoros: Int?,
    val completed: Boolean = false,
    val createdAt: Long,
    val completedAt: Long?
)

data class UserPreferences(
    val pomodoroDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val longBreakInterval: Int = 4,
    val notificationsEnabled: Boolean = true,
    val selectedSoundUri: String?,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    val theme: Theme = Theme.SYSTEM
)
```

## 5. 实施细节
### 5.1 定时器实现
- 使用前台 Service + CountDownTimer，前台通知保持进程存活，广播剩余时间用于 UI 更新

### 5.2 番茄动画
- Compose `Canvas` 绘制番茄路径，使用 `clipPath` 按进度填充

### 5.3 后台执行
- 前台服务负责活跃计时；WorkManager 在应用被杀时发送定时通知；可选 WakeLock 确保计时

### 5.4 通知系统
- 创建高优先级渠道，支持自定义声音/振动；提供完成通知与常驻通知动作（暂停、停止、开始休息）

### 5.5 数据库
```kotlin
@Database(entities = [PomodoroSession::class, Task::class], version = 1)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun taskDao(): TaskDao
}

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<PomodoroSession>>

    @Query("SELECT * FROM pomodoro_sessions WHERE startTime BETWEEN :startDate AND :endDate")
    fun getSessionsByDateRange(startDate: Long, endDate: Long): Flow<List<PomodoroSession>>

    @Insert suspend fun insertSession(session: PomodoroSession)
}
```

## 6. 性能优化
- UI：长列表用 LazyColumn，历史分页，统计缓存，Compose 中善用 `remember`/`derivedStateOf`
- 电量：不必要时释放 WakeLock，Doze 场景用 AlarmManager 精准唤醒，批量写库，最小化后台任务
- 内存：ViewModel 抗配置变更，取消协程，优先 Flow，严格生命周期感知

## 7. 适配与资源
- 响应式：ConstraintLayout/Compose 约束，按尺寸定义 `dimens`，必要时横屏布局，使用矢量图
- 资源限定符：`values/`、`values-sw600dp/`、`values-sw720dp/` 分级尺寸资源

## 8. 可选功能
- 云同步：Firebase Firestore 或自建 REST，时间戳冲突解决，WorkManager 后台同步，登录方式 Google/邮箱
- 主题切换：自定义 `PomodoroTheme`，根据系统/手动切换浅深色，番茄色主色系

## 9. 测试策略
- 单测：ViewModel 逻辑、UseCase、计时计算、数据转换
- 集成：Room 操作、Repository、Service 交互
- UI：Compose 流程、导航、无障碍测试

## 10. 发展阶段（时间预估）
- 阶段一 MVP（2-3 周）：基本计时、启动/暂停/重置、番茄动画、本地通知、基础偏好
- 阶段二 核心（2-3 周）：任务管理、历史、统计/图表、自定义声音、主题切换
- 阶段三 Polish（1-2 周）：UI 优化、性能、无障碍、修复
- 阶段四 可选（2-3 周）：云同步、高级统计、小部件、Wear OS

## 11. 代码示例：主计时器 ViewModel
```kotlin
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository,
    private val preferencesRepository: PreferencesRepository,
    private val timerManager: TimerManager
) : ViewModel() {

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    val progress: StateFlow<Float> = combine(timeRemaining, timerState) { time, state ->
        val totalTime = when (state) {
            is TimerState.Running -> state.totalDuration
            else -> preferencesRepository.getPomodoroDuration() * 60 * 1000L
        }
        if (totalTime > 0) time.toFloat() / totalTime else 0f
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    fun startTimer(sessionType: SessionType = SessionType.WORK) {
        viewModelScope.launch {
            val duration = getDurationForType(sessionType)
            timerManager.start(duration) { remaining ->
                _timeRemaining.value = remaining
                if (remaining == 0L) handleTimerComplete(sessionType)
            }
            _timerState.value = TimerState.Running(sessionType, duration)
        }
    }

    fun pauseTimer() {
        timerManager.pause()
        _timerState.value = TimerState.Paused
    }

    fun resumeTimer() {
        timerManager.resume()
        _timerState.value = (_timerState.value as? TimerState.Paused)
            ?.let { TimerState.Running(it.sessionType, it.totalDuration) }
            ?: TimerState.Idle
    }

    fun resetTimer() {
        timerManager.stop()
        _timerState.value = TimerState.Idle
        _timeRemaining.value = 0L
    }

    private suspend fun handleTimerComplete(sessionType: SessionType) {
        pomodoroRepository.insertSession(
            PomodoroSession(
                taskId = currentTaskId,
                startTime = System.currentTimeMillis() - getDurationForType(sessionType),
                endTime = System.currentTimeMillis(),
                duration = getDurationForType(sessionType).toInt() / 60000,
                type = sessionType,
                completed = true
            )
        )
        _timerState.value = TimerState.Completed(sessionType)
    }
}

sealed class TimerState {
    object Idle : TimerState()
    data class Running(val sessionType: SessionType, val totalDuration: Long) : TimerState()
    object Paused : TimerState()
    data class Completed(val sessionType: SessionType) : TimerState()
}
```

## 12. 安全
- 敏感数据按需加密（云同步时）
- 验证所有输入；所有网络通信使用 HTTPS，建议证书锁定
- 遵循 Android 安全最佳实践

## 13. 无障碍
- 完整内容描述，TalkBack 支持
- 最小触控目标 48dp；颜色对比符合 WCAG AA
- 支持大字体

## 14. 结论
该设计为生产级番茄钟应用提供蓝图，兼顾性能、体验与可维护性，并支持阶段化迭代与未来扩展。

