package im.hengxin.permissionlab.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import im.hengxin.permissionlab.core.AlarmScheduler
import im.hengxin.permissionlab.core.LabStore
import im.hengxin.permissionlab.core.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val allowed = setOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_LOCKED_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED)
        if (intent.action !in allowed) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val state = LabStore.state(context).first()
                if (state.alarmEnabled && state.alarmAt > System.currentTimeMillis()) {
                    AlarmScheduler.schedule(context, state.alarmAt)
                    NotificationHelper.showPersistent(context, "已恢复待办提醒", "开机后已重新注册保存的闹钟。")
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
