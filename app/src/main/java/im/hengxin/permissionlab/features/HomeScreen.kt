package im.hengxin.permissionlab.features

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page

@Composable
fun HomeScreen() {
    Page {
        LabCard(
            "模板目标",
            "本项目是 Kotlin 原生 Android 权限申请示例。每类权限都拆到独立页面和独立源码文件，按钮会触发真实权限申请或系统设置入口，并提供最小可验证使用场景。",
        )
        LabCard(
            "测试建议",
            "在小米、OPPO、vivo、荣耀等系统上，除标准权限外，还需要手动检查通知样式、自启动、省电策略、锁屏显示、后台定位、悬浮窗和精确闹钟。系统页提供了常用入口。",
        )
        Text("底部切换不同权限页面。")
    }
}
