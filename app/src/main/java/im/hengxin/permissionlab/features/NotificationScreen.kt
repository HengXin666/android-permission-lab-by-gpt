package im.hengxin.permissionlab.features

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import im.hengxin.permissionlab.core.AlarmScheduler
import im.hengxin.permissionlab.core.LabStore
import im.hengxin.permissionlab.core.NotificationHelper
import im.hengxin.permissionlab.core.notificationPermission
import im.hengxin.permissionlab.core.openNotificationSettings
import im.hengxin.permissionlab.core.requestMissing
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen() {
    val context = LocalContext.current
    val state by LabStore.state(context).collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    val permissions = notificationPermission()?.let { listOf(it) }.orEmpty()
    var secondsText by remember { mutableStateOf("5") }

    Page {
        LabCard(
            "通知栏权限",
            "Android 13+ 需要 POST_NOTIFICATIONS。点击后发送普通通知，用于验证通知栏是否弹出。",
        ) {
            ActionRow(
                "申请并发送",
                {
                    launcher.requestMissing(context, permissions)
                    NotificationHelper.showSimple(context, "普通通知测试", "如果已授权，这条通知会显示在通知栏。")
                },
                "通知设置",
                context::openNotificationSettings,
            )
        }
        LabCard(
            "常驻通知和锁屏显示",
            "创建低优先级 ongoing 通知，适合倒数日、后台采集等状态。锁屏显示还受系统通知分类和 ROM 策略控制。",
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("常驻通知")
                Switch(
                    checked = state?.persistentNotificationEnabled == true,
                    onCheckedChange = { checked ->
                        launcher.requestMissing(context, permissions)
                        if (checked) {
                            NotificationHelper.showPersistent(context, "倒数日常驻测试", "距离目标日还有 10 天。")
                        } else {
                            NotificationHelper.cancelPersistent(context)
                        }
                        scope.launch { LabStore.savePersistentNotification(context, checked) }
                    },
                )
            }
        }
        LabCard(
            "后台定时通知",
            "用 AlarmManager 在几秒后发通知。设置 5 秒后，立刻清最近任务测试；如果是系统“强行停止”，Android 不会再投递。",
        ) {
            OutlinedTextField(
                value = secondsText,
                onValueChange = { secondsText = it.filter(Char::isDigit).ifBlank { "5" } },
                label = { Text("几秒后发送") },
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("启用后台定时通知")
                Switch(
                    checked = state?.scheduledNotificationEnabled == true,
                    onCheckedChange = { checked ->
                        launcher.requestMissing(context, permissions)
                        if (checked) {
                            val delaySeconds = secondsText.toLongOrNull()?.coerceAtLeast(1L) ?: 5L
                            val triggerAt = System.currentTimeMillis() + delaySeconds * 1_000L
                            AlarmScheduler.scheduleNotification(context, triggerAt)
                            scope.launch { LabStore.saveScheduledNotification(context, triggerAt, true) }
                        } else {
                            AlarmScheduler.cancelNotification(context)
                            scope.launch { LabStore.saveScheduledNotification(context, 0L, false) }
                        }
                    },
                )
            }
        }
        LabCard(
            "锁屏栏通知测试",
            "启用后会用高优先级闹钟通道和 public visibility 在几秒后发送。请先锁屏等待；若仍不显示，通常需要到系统通知设置里打开锁屏通知/横幅/重要通知。",
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("启用锁屏通知")
                Switch(
                    checked = state?.lockScreenNotificationEnabled == true,
                    onCheckedChange = { checked ->
                        launcher.requestMissing(context, permissions)
                        if (checked) {
                            val delaySeconds = secondsText.toLongOrNull()?.coerceAtLeast(1L) ?: 5L
                            val triggerAt = System.currentTimeMillis() + delaySeconds * 1_000L
                            AlarmScheduler.scheduleLockScreenNotification(context, triggerAt)
                            scope.launch { LabStore.saveLockScreenNotification(context, triggerAt, true) }
                        } else {
                            AlarmScheduler.cancelLockScreenNotification(context)
                            scope.launch { LabStore.saveLockScreenNotification(context, 0L, false) }
                        }
                    },
                )
            }
            ActionRow("通知设置", context::openNotificationSettings)
        }
        LabCard(
            "兼容说明",
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                "当前系统需要运行时通知权限：${Manifest.permission.POST_NOTIFICATIONS}"
            } else {
                "当前系统通知权限主要通过系统设置控制。"
            },
        )
    }
}
