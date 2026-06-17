package im.hengxin.permissionlab.features

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import im.hengxin.permissionlab.core.NotificationHelper
import im.hengxin.permissionlab.core.notificationPermission
import im.hengxin.permissionlab.core.openNotificationSettings
import im.hengxin.permissionlab.core.requestMissing
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page

@Composable
fun NotificationScreen() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    val permissions = notificationPermission()?.let { listOf(it) }.orEmpty()

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
            ActionRow(
                "显示常驻",
                {
                    launcher.requestMissing(context, permissions)
                    NotificationHelper.showPersistent(context, "倒数日常驻测试", "距离目标日还有 10 天。")
                },
                "取消常驻",
                { NotificationHelper.cancelPersistent(context) },
            )
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
