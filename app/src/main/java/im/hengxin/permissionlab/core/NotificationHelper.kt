package im.hengxin.permissionlab.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import im.hengxin.permissionlab.MainActivity
import im.hengxin.permissionlab.R

object NotificationHelper {
    const val CHANNEL_GENERAL = "general"
    const val CHANNEL_PERSISTENT = "persistent"
    const val CHANNEL_ALARM = "alarm"
    const val ID_SIMPLE = 1001
    const val ID_PERSISTENT = 1002
    const val ID_ALARM = 1003
    const val ID_SENSOR = 1004
    const val ID_SCHEDULED = 1005
    const val ID_LOCK_SCREEN = 1006

    fun ensureChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel(CHANNEL_GENERAL, "普通通知", NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel(CHANNEL_PERSISTENT, "常驻与锁屏", NotificationManager.IMPORTANCE_LOW).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            },
            NotificationChannel(CHANNEL_ALARM, "闹钟提醒", NotificationManager.IMPORTANCE_HIGH).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
        )
        manager.createNotificationChannels(channels)
    }

    fun showSimple(context: Context, title: String, text: String) {
        ensureChannels(context)
        context.getSystemService(NotificationManager::class.java)
            .notify(ID_SIMPLE, base(context, CHANNEL_GENERAL, title, text).build())
    }

    fun showPersistent(context: Context, title: String, text: String) {
        ensureChannels(context)
        val notification = base(context, CHANNEL_PERSISTENT, title, text)
            .setOngoing(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(ID_PERSISTENT, notification)
    }

    fun cancelPersistent(context: Context) {
        context.getSystemService(NotificationManager::class.java).cancel(ID_PERSISTENT)
    }

    fun alarmNotification(context: Context, title: String, text: String): Notification {
        ensureChannels(context)
        return base(context, CHANNEL_ALARM, title, text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    fun lockScreenNotification(context: Context, title: String, text: String): Notification {
        ensureChannels(context)
        return base(context, CHANNEL_ALARM, title, text)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(contentIntent(context), true)
            .build()
    }

    fun sensorNotification(context: Context, text: String): Notification {
        ensureChannels(context)
        return base(context, CHANNEL_PERSISTENT, "后台传感器采集中", text)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun contentIntent(context: Context): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun base(context: Context, channelId: String, title: String, text: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_permission)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(contentIntent(context))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }
}
