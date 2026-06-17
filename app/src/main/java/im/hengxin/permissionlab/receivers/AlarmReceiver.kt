package im.hengxin.permissionlab.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat
import im.hengxin.permissionlab.core.AlarmScheduler
import im.hengxin.permissionlab.core.LabStore
import im.hengxin.permissionlab.core.NotificationHelper
import im.hengxin.permissionlab.core.notificationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_ALARM) return
        val permission = notificationPermission()
        if (permission != null && ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) return
        val kind = intent.getStringExtra(AlarmScheduler.EXTRA_KIND) ?: AlarmScheduler.KIND_ALARM
        val notification = when (kind) {
            AlarmScheduler.KIND_NOTIFICATION -> NotificationHelper.alarmNotification(
                context,
                "后台定时通知",
                "这条通知由 AlarmManager 在 App 进程不在前台时触发。",
            )
            AlarmScheduler.KIND_LOCK_SCREEN -> NotificationHelper.lockScreenNotification(
                context,
                "锁屏通知测试",
                "锁屏后等待触发。若锁屏不显示，请检查系统通知分类、锁屏通知和后台弹出权限。",
            )
            else -> NotificationHelper.alarmNotification(
                context,
                "闹钟/待办提醒",
                "这是通过 AlarmManager 精确闹钟触发的提醒，可用于测试清后台后的提醒能力。",
            )
        }
        val id = when (kind) {
            AlarmScheduler.KIND_NOTIFICATION -> NotificationHelper.ID_SCHEDULED
            AlarmScheduler.KIND_LOCK_SCREEN -> NotificationHelper.ID_LOCK_SCREEN
            else -> NotificationHelper.ID_ALARM
        }
        runCatching {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (kind) {
                AlarmScheduler.KIND_NOTIFICATION -> LabStore.saveScheduledNotification(context, 0L, false)
                AlarmScheduler.KIND_LOCK_SCREEN -> LabStore.saveLockScreenNotification(context, 0L, false)
                else -> LabStore.saveAlarm(context, 0L, false)
            }
        }
    }

    companion object {
        const val ACTION_ALARM = "im.hengxin.permissionlab.ACTION_ALARM"
    }
}
