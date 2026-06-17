package im.hengxin.permissionlab.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat
import im.hengxin.permissionlab.core.NotificationHelper
import im.hengxin.permissionlab.core.notificationPermission

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_ALARM) return
        val permission = notificationPermission()
        if (permission != null && ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) return
        val notification = NotificationHelper.alarmNotification(
            context,
            "闹钟/待办提醒",
            "这是通过 AlarmManager 精确闹钟触发的提醒，可用于测试清后台后的提醒能力。",
        )
        runCatching {
            NotificationManagerCompat.from(context).notify(NotificationHelper.ID_ALARM, notification)
        }
    }

    companion object {
        const val ACTION_ALARM = "im.hengxin.permissionlab.ACTION_ALARM"
    }
}
