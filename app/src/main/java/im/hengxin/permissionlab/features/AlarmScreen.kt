package im.hengxin.permissionlab.features

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
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
    var secondsText by remember { mutableStateOf("5") }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA) }

    LaunchedEffect(Unit) {
        if (!context.canScheduleExactAlarms()) {
            context.openExactAlarmSettings()
        }
    }

    Page {
        LabCard(
            "秒级闹钟测试",
            "使用 AlarmManager 注册系统闹钟，并把开关和触发时间持久化。建议设 5 秒，点开关后立刻清最近任务并锁屏测试。注意：系统设置里的“强行停止”会让所有本地闹钟失效。",
        ) {
            OutlinedTextField(
                value = secondsText,
                onValueChange = { secondsText = it.filter(Char::isDigit).ifBlank { "5" } },
                label = { Text("几秒后提醒") },
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                Text("启用闹钟")
                Switch(
                    checked = state?.alarmEnabled == true,
                    onCheckedChange = { checked ->
                        if (checked) {
                            val delaySeconds = secondsText.toLongOrNull()?.coerceAtLeast(1L) ?: 5L
                            val triggerAt = System.currentTimeMillis() + delaySeconds * 1_000L
                            AlarmScheduler.scheduleAlarm(context, triggerAt)
                            scope.launch { LabStore.saveAlarm(context, triggerAt, true) }
                        } else {
                            AlarmScheduler.cancelAlarm(context)
                            scope.launch { LabStore.saveAlarm(context, 0L, false) }
                        }
                    },
                )
            }
        }
        LabCard(
            "当前状态",
            "精确闹钟权限：${if (context.canScheduleExactAlarms()) "可用" else "不可用"}\n" +
                "开关状态：${if (state?.alarmEnabled == true) "开" else "关"}\n" +
                "已保存提醒：${state?.alarmAt?.takeIf { it > 0 }?.let { dateFormat.format(Date(it)) } ?: "无"}\n" +
                "清最近任务可测；强行停止不可测，这是 Android 系统限制。",
        ) {
            ActionRow("精确闹钟设置", context::openExactAlarmSettings, "省电白名单", context::openBatteryOptimizationSettings)
        }
    }
}
