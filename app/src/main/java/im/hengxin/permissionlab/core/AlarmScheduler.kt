package im.hengxin.permissionlab.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import im.hengxin.permissionlab.receivers.AlarmReceiver

object AlarmScheduler {
    private const val REQUEST_CODE_ALARM = 4401
    private const val REQUEST_CODE_NOTIFICATION = 4402
    private const val REQUEST_CODE_LOCK_SCREEN = 4403
    const val EXTRA_KIND = "kind"
    const val KIND_ALARM = "alarm"
    const val KIND_NOTIFICATION = "notification"
    const val KIND_LOCK_SCREEN = "lock_screen"

    fun scheduleAlarm(context: Context, triggerAtMillis: Long) {
        schedule(context, triggerAtMillis, KIND_ALARM, REQUEST_CODE_ALARM, useAlarmClock = true)
    }

    fun scheduleNotification(context: Context, triggerAtMillis: Long) {
        schedule(context, triggerAtMillis, KIND_NOTIFICATION, REQUEST_CODE_NOTIFICATION, useAlarmClock = false)
    }

    fun scheduleLockScreenNotification(context: Context, triggerAtMillis: Long) {
        schedule(context, triggerAtMillis, KIND_LOCK_SCREEN, REQUEST_CODE_LOCK_SCREEN, useAlarmClock = true)
    }

    private fun schedule(
        context: Context,
        triggerAtMillis: Long,
        kind: String,
        requestCode: Int,
        useAlarmClock: Boolean,
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = alarmIntent(context, requestCode, kind)
        if (useAlarmClock) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAtMillis, contentIntent(context)), pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancelAlarm(context: Context) {
        cancel(context, REQUEST_CODE_ALARM, KIND_ALARM)
    }

    fun cancelNotification(context: Context) {
        cancel(context, REQUEST_CODE_NOTIFICATION, KIND_NOTIFICATION)
    }

    fun cancelLockScreenNotification(context: Context) {
        cancel(context, REQUEST_CODE_LOCK_SCREEN, KIND_LOCK_SCREEN)
    }

    private fun cancel(context: Context, requestCode: Int, kind: String) {
        context.getSystemService(AlarmManager::class.java).cancel(alarmIntent(context, requestCode, kind))
    }

    private fun alarmIntent(context: Context, requestCode: Int, kind: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, AlarmReceiver::class.java)
                .setAction(AlarmReceiver.ACTION_ALARM)
                .putExtra(EXTRA_KIND, kind),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun contentIntent(context: Context): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, im.hengxin.permissionlab.MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
