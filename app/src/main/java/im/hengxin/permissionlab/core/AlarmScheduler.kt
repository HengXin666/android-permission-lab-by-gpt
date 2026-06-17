package im.hengxin.permissionlab.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import im.hengxin.permissionlab.receivers.AlarmReceiver

object AlarmScheduler {
    private const val REQUEST_CODE = 4401

    fun schedule(context: Context, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = alarmIntent(context)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java).cancel(alarmIntent(context))
    }

    private fun alarmIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java).setAction(AlarmReceiver.ACTION_ALARM),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
