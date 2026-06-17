package im.hengxin.permissionlab.features

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import im.hengxin.permissionlab.core.mediaReadPermissions
import im.hengxin.permissionlab.core.requestMissing
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page

@Composable
fun FileScreen() {
    val context = LocalContext.current
    var selected by remember { mutableStateOf("未选择文件") }
    var preview by remember { mutableStateOf("") }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    val openDocument = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            selected = uri.toString()
            preview = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                reader.readText().take(800)
            }.orEmpty()
        }
    }

    Page {
        LabCard(
            "系统文件选择器",
            "通过 Storage Access Framework 读取用户选择的文件。这个方式不要求 MANAGE_EXTERNAL_STORAGE，适合模板项目演示最小权限读取。",
        ) {
            ActionRow("选择文本文件", { openDocument.launch(arrayOf("text/*", "application/json", "*/*")) })
            Text(selected)
        }
        LabCard(
            "媒体读取权限",
            "Android 13+ 使用 READ_MEDIA_IMAGES/VIDEO；旧版本使用 READ_EXTERNAL_STORAGE。点击后只申请权限，不绕过用户选择。",
        ) {
            ActionRow("申请媒体读取", { permissionLauncher.requestMissing(context, mediaReadPermissions()) })
        }
        LabCard("读取回调预览", preview.ifBlank { "选择文本文件后显示前 800 字。" })
    }
}
