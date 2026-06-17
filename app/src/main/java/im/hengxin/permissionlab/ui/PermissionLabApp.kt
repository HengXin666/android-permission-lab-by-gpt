package im.hengxin.permissionlab.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import im.hengxin.permissionlab.features.AlarmScreen
import im.hengxin.permissionlab.features.CameraScreen
import im.hengxin.permissionlab.features.FileScreen
import im.hengxin.permissionlab.features.HomeScreen
import im.hengxin.permissionlab.features.NotificationScreen
import im.hengxin.permissionlab.features.OemScreen
import im.hengxin.permissionlab.features.SensorScreen
import im.hengxin.permissionlab.features.SmsScreen
import im.hengxin.permissionlab.features.StorageScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionLabApp() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: Route.Home.path

    Scaffold(
        topBar = { TopAppBar(title = { Text("权限申请模板") }) },
        bottomBar = {
            NavigationBar {
                routes.forEach { route ->
                    NavigationBarItem(
                        selected = currentRoute == route.path,
                        onClick = {
                            navController.navigate(route.path) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(route.icon, route.title) },
                        label = { Text(route.title) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.path,
            modifier = Modifier.padding(padding),
        ) {
            composable(Route.Home.path) { HomeScreen() }
            composable(Route.Notifications.path) { NotificationScreen() }
            composable(Route.Alarm.path) { AlarmScreen() }
            composable(Route.Sensors.path) { SensorScreen() }
            composable(Route.Storage.path) { StorageScreen() }
            composable(Route.Files.path) { FileScreen() }
            composable(Route.Camera.path) { CameraScreen() }
            composable(Route.Sms.path) { SmsScreen() }
            composable(Route.Oem.path) { OemScreen() }
        }
    }
}

private val routes = listOf(
    Route.Home,
    Route.Notifications,
    Route.Alarm,
    Route.Sensors,
    Route.Storage,
    Route.Files,
    Route.Camera,
    Route.Sms,
    Route.Oem,
)

private sealed class Route(val path: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : Route("home", "总览", Icons.Filled.Home)
    data object Notifications : Route("notifications", "通知", Icons.Filled.Notifications)
    data object Alarm : Route("alarm", "闹钟", Icons.Filled.Alarm)
    data object Sensors : Route("sensors", "传感器", Icons.Filled.Sensors)
    data object Storage : Route("storage", "存储", Icons.Filled.Article)
    data object Files : Route("files", "文件", Icons.Filled.Folder)
    data object Camera : Route("camera", "相机", Icons.Filled.CameraAlt)
    data object Sms : Route("sms", "短信", Icons.Filled.Sms)
    data object Oem : Route("oem", "系统", Icons.Filled.SettingsApplications)
}
