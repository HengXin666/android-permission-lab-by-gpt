package im.hengxin.permissionlab.core

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.missingPermissions(permissions: List<String>): List<String> {
    return permissions.filterNot { hasPermission(it) }
}

fun ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>.requestMissing(
    context: Context,
    permissions: List<String>,
) {
    val missing = context.missingPermissions(permissions)
    if (missing.isNotEmpty()) {
        launch(missing.toTypedArray())
    }
}

fun Activity.shouldExplain(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

fun Context.openAppSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

fun Context.openNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun Context.openExactAlarmSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

fun Context.openOverlaySettings() {
    startActivity(
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

fun Context.openBatteryOptimizationSettings() {
    val powerManager = getSystemService(PowerManager::class.java)
    if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
        startActivity(
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData(Uri.parse("package:$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    } else {
        startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

fun Context.canScheduleExactAlarms(): Boolean {
    val alarmManager = getSystemService(AlarmManager::class.java)
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
}

fun notificationPermission(): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null
}

fun mediaReadPermissions(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
