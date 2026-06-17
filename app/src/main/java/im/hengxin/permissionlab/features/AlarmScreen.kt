package im.hengxin.permissionlab.features

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import im.hengxin.permissionlab.core.AlarmScheduler
import im.hengxin.permissionlab.core.LabStore
import im.hengxin.permissionlab.core.canScheduleExactAlarms
import im.hengxin.permissionlab.core.openBatteryOptimizationSettings
import im.hengxin.permissionlab.core.openExactAlarmSettings
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlarmScreen() {
    val context = LocalContext.current
    val state by LabStore.state(context).collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    var minutesText by remember { mutableStateOf("1") }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA) }

    LaunchedEffect(Unit) {
        if (!context.canScheduleExactAlarms()) {
            context.openExactAlarmSettings()
        }
    }

    Page {
        LabCard(
            "清后台后仍触发",
            "使用 AlarmManager.setExactAndAllowWhileIdle 注册精确闹钟，并把时间持久化。杀后台后系统仍应触发；重启后 BootReceiver 会重新注册未来的提醒。",
        ) {
            OutlinedTextField(
                value = minutesText,
                onValueChange = { minutesText = it.filter(Char::isDigit).ifBlank { "1" } },
                label = { Text("几分钟后提醒") },
            )
            ActionRow(
                "注册闹钟",
                {
                    val delayMinutes = minutesText.toLongOrNull()?.coerceAtLeast(1L) ?: 1L
                    val triggerAt = System.currentTimeMillis() + delayMinutes * 60_000L
                    AlarmScheduler.schedule(context, triggerAt)
                    scope.launch { LabStore.saveAlarm(context, triggerAt, true) }
                },
                "取消",
                {
                    AlarmScheduler.cancel(context)
                    scope.launch { LabStore.saveAlarm(context, 0L, false) }
                },
            )
        }
        LabCard(
            "当前状态",
            "精确闹钟权限：${if (context.canScheduleExactAlarms()) "可用" else "不可用"}\n" +
                "已保存提醒：${state?.alarmAt?.takeIf { it > 0 }?.let { dateFormat.format(Date(it)) } ?: "无"}",
        ) {
            ActionRow("精确闹钟设置", context::openExactAlarmSettings, "省电白名单", context::openBatteryOptimizationSettings)
        }
    }
}
