package im.hengxin.permissionlab.features

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import im.hengxin.permissionlab.core.openBatteryOptimizationSettings
import im.hengxin.permissionlab.core.requestMissing
import im.hengxin.permissionlab.services.SensorForegroundService
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page

@Composable
fun SensorScreen() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    val permissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add(Manifest.permission.ACTIVITY_RECOGNITION)
    }

    Page {
        LabCard(
            "加速度传感器",
            "加速度传感器通常不需要运行时权限。页面会启动前台服务持续采集，并在常驻通知中更新最近数据。",
        ) {
            ActionRow(
                "启动后台采集",
                {
                    launcher.requestMissing(context, permissions)
                    ContextCompat.startForegroundService(context, Intent(context, SensorForegroundService::class.java))
                },
                "停止",
                { context.stopService(Intent(context, SensorForegroundService::class.java)) },
            )
        }
        LabCard(
            "GPS 与后台限制",
            "示例请求前台定位并通过前台服务在后台继续工作。Android 10+ 的真正后台定位 ACCESS_BACKGROUND_LOCATION 需要单独跳转系统权限页授权，不能和前台定位同批弹窗。",
        ) {
            ActionRow("申请定位", { launcher.requestMissing(context, permissions) }, "省电白名单", context::openBatteryOptimizationSettings)
        }
    }
}
