package im.hengxin.permissionlab.features

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import im.hengxin.permissionlab.core.requestMissing
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var lastPhoto by remember { mutableStateOf<Uri?>(null) }
    var status by remember { mutableStateOf("未拍照") }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        status = if (ok) "拍照成功：$lastPhoto" else "拍照取消或失败"
    }

    Page {
        LabCard(
            "摄像头权限",
            "申请 CAMERA 后调用系统相机写入 MediaStore。前后置切换由系统相机界面完成；如果要自定义预览，可在此页面替换为 CameraX。",
        ) {
            ActionRow(
                "申请权限",
                { permissionLauncher.requestMissing(context, listOf(Manifest.permission.CAMERA)) },
                "拍照",
                {
                    val uri = createImageUri(context)
                    lastPhoto = uri
                    takePicture.launch(uri)
                },
            )
            Text(status)
        }
    }
}

private fun createImageUri(context: android.content.Context): Uri {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "permission-lab-${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return requireNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
}
