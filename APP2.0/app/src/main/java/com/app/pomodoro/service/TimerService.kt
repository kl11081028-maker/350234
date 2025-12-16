package com.app.pomodoro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.app.pomodoro.R
import com.app.pomodoro.ui.MainActivity
import com.app.pomodoro.ui.timer.SessionType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class TimerService : Service() {

    @Inject lateinit var timerManager: TimerManager

    private val scope = CoroutineScope(Dispatchers.Default)
    private var notificationJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getLongExtra(EXTRA_DURATION, DEFAULT_DURATION)
                val sessionType = intent.getStringExtra(EXTRA_SESSION_TYPE)?.let {
                    runCatching { SessionType.valueOf(it) }.getOrDefault(SessionType.WORK)
                } ?: SessionType.WORK
                startForeground(NOTIFICATION_ID, buildNotification(formatTime(duration)))
                timerManager.start(
                    durationMillis = duration,
                    sessionType = sessionType,
                    onComplete = { stopSelf() },
                    onTick = { remaining ->
                        sendBroadcast(Intent(ACTION_TICK).putExtra(EXTRA_REMAINING, remaining))
                    }
                )
                observeAndUpdateNotification()
            }
            ACTION_PAUSE -> {
                timerManager.pause()
            }
            ACTION_RESUME -> {
                timerManager.resume(
                    onComplete = { stopSelf() },
                    onTick = { remaining ->
                        sendBroadcast(Intent(ACTION_TICK).putExtra(EXTRA_REMAINING, remaining))
                    }
                )
            }
            ACTION_STOP -> {
                timerManager.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        notificationJob?.cancel()
        super.onDestroy()
    }

    private fun observeAndUpdateNotification() {
        notificationJob?.cancel()
        notificationJob = scope.launch {
            timerManager.remaining.collectLatest { remaining ->
                updateNotification(formatTime(remaining))
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(content: String) {
        val notification = buildNotification(content)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(content: String): Notification {
        val pauseIntent = actionPendingIntent(ACTION_PAUSE)
        val resumeIntent = actionPendingIntent(ACTION_RESUME)
        val stopIntent = actionPendingIntent(ACTION_STOP)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setContentIntent(mainPendingIntent())
            .addAction(0, getString(R.string.action_pause), pauseIntent)
            .addAction(0, getString(R.string.action_start), resumeIntent)
            .addAction(0, getString(R.string.action_reset), stopIntent)
            .build()
    }

    private fun mainPendingIntent() = TaskStackBuilder.create(this).run {
        addNextIntent(Intent(this@TimerService, MainActivity::class.java))
        getPendingIntent(0, PendingIntentCompat.FLAG_IMMUTABLE or PendingIntentCompat.FLAG_UPDATE_CURRENT)
    }

    private fun actionPendingIntent(action: String) =
        PendingIntentCompat.getService(
            this,
            action.hashCode(),
            Intent(this, TimerService::class.java).setAction(action),
            PendingIntentCompat.FLAG_IMMUTABLE or PendingIntentCompat.FLAG_UPDATE_CURRENT
        )

    private fun formatTime(millis: Long): String {
        val duration = millis.milliseconds
        val minutes = duration.inWholeMinutes
        val seconds = (duration.inWholeSeconds % 60)
        return "%02d:%02d".format(minutes, seconds)
    }

    companion object {
        const val ACTION_START = "com.app.pomodoro.action.START"
        const val ACTION_PAUSE = "com.app.pomodoro.action.PAUSE"
        const val ACTION_RESUME = "com.app.pomodoro.action.RESUME"
        const val ACTION_STOP = "com.app.pomodoro.action.STOP"
        const val ACTION_TICK = "com.app.pomodoro.action.TICK"

        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_SESSION_TYPE = "extra_session_type"
        const val EXTRA_REMAINING = "extra_remaining"

        private const val CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1001
        private const val DEFAULT_DURATION = 25 * 60 * 1000L

        fun startCommand(context: Context, durationMillis: Long, sessionType: SessionType) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION, durationMillis)
                putExtra(EXTRA_SESSION_TYPE, sessionType.name)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun pauseCommand(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply { action = ACTION_PAUSE }
            ContextCompat.startForegroundService(context, intent)
        }

        fun resumeCommand(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply { action = ACTION_RESUME }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopCommand(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply { action = ACTION_STOP }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}

