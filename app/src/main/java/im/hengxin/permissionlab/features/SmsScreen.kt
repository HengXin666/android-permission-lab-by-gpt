package im.hengxin.permissionlab.features

import android.Manifest
import android.provider.Telephony
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
fun SmsScreen() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    var rows by remember { mutableStateOf(listOf("未读取")) }

    Page {
        LabCard(
            "短信读取",
            "申请 READ_SMS 后读取系统短信收件箱最近 5 条。该权限属于高敏感权限，上架应用商店通常需要强理由；本项目仅作本机权限模板测试。",
        ) {
            ActionRow(
                "申请权限",
                { launcher.requestMissing(context, listOf(Manifest.permission.READ_SMS)) },
                "读取最近短信",
                { rows = readSms(context) },
            )
        }
        LabCard("读取结果", rows.joinToString("\n\n")) {
            Text("如无权限或无短信，会显示为空或失败信息。")
        }
    }
}

private fun readSms(context: android.content.Context): List<String> {
    return runCatching {
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE)
        context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            null,
            null,
            "${Telephony.Sms.DATE} DESC",
        )?.use { cursor ->
            buildList {
                val addressIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
                while (cursor.moveToNext() && size < 5) {
                    add("${cursor.getString(addressIndex)}\n${cursor.getLong(dateIndex)}\n${cursor.getString(bodyIndex).take(120)}")
                }
            }
        }.orEmpty()
    }.getOrElse { listOf("读取失败：${it.message}") }.ifEmpty { listOf("没有读取到短信") }
}
