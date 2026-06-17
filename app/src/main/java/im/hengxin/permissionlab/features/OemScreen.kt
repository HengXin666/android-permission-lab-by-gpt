package im.hengxin.permissionlab.features

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import im.hengxin.permissionlab.core.OemIntents
import im.hengxin.permissionlab.core.openAppSettings
import im.hengxin.permissionlab.core.openBatteryOptimizationSettings
import im.hengxin.permissionlab.core.openExactAlarmSettings
import im.hengxin.permissionlab.core.openNotificationSettings
import im.hengxin.permissionlab.core.openOverlaySettings
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page

@Composable
fun OemScreen() {
    val context = LocalContext.current

    Page {
        LabCard(
            "国产系统专项",
            "MIUI/ColorOS/OriginOS/MagicOS/EMUI 往往还有 ROM 私有开关。标准 Android API 不能直接授予这些能力，只能引导用户到设置页确认。",
        )
        LabCard(
            "后台存活",
            "用于测试杀后台后闹钟、通知、前台服务是否还能工作。重点检查自启动、省电策略、后台弹出界面、锁屏通知。",
        ) {
            ActionRow("自启动", { OemIntents.openAutoStart(context) }, "省电策略", { OemIntents.openPowerManager(context) })
            ActionRow("电池优化白名单", context::openBatteryOptimizationSettings)
        }
        LabCard(
            "通知与弹窗",
            "通知栏、锁屏通知、横幅弹窗、悬浮窗和后台弹出界面通常是分开的系统开关。",
        ) {
            ActionRow("通知设置", context::openNotificationSettings, "悬浮窗", context::openOverlaySettings)
        }
        LabCard(
            "精确闹钟与应用详情",
            "Android 12+ 精确闹钟可能需要单独授权。应用详情页可检查权限列表、耗电、锁屏显示等厂商设置。",
        ) {
            ActionRow("精确闹钟", context::openExactAlarmSettings, "应用详情", context::openAppSettings)
        }
    }
}
