package im.hengxin.permissionlab.features

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import im.hengxin.permissionlab.core.LabStore
import im.hengxin.permissionlab.ui.ActionRow
import im.hengxin.permissionlab.ui.LabCard
import im.hengxin.permissionlab.ui.Page
import kotlinx.coroutines.launch

@Composable
fun StorageScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by LabStore.state(context).collectAsState(initial = null)
    var draft by remember(state?.note) { mutableStateOf(state?.note.orEmpty()) }

    Page {
        LabCard(
            "持久化数据",
            "使用 Jetpack DataStore 保存文本和闹钟配置。重启应用后仍能读取，用于权限测试中的状态记录。",
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text("本地备注") },
            )
            ActionRow("保存", { scope.launch { LabStore.saveNote(context, draft) } })
        }
        LabCard("已保存内容", state?.note?.ifBlank { "空" } ?: "加载中")
    }
}
