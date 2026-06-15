package im.hengxin.permissionlab

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.getcapacitor.JSObject
import com.getcapacitor.PermissionCallback
import com.getcapacitor.PermissionState
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission

@CapacitorPlugin(
    name = "AndroidPermissionBroker",
    permissions = [
        Permission(strings = [Manifest.permission.CAMERA], alias = "camera"),
        Permission(strings = [Manifest.permission.RECORD_AUDIO], alias = "microphone"),
        Permission(strings = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION], alias = "location.foreground"),
        Permission(strings = [Manifest.permission.ACCESS_BACKGROUND_LOCATION], alias = "location.background"),
        Permission(strings = [Manifest.permission.POST_NOTIFICATIONS], alias = "notification"),
        Permission(strings = [Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO], alias = "photo.read"),
        Permission(strings = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT], alias = "bluetooth"),
        Permission(strings = [Manifest.permission.NEARBY_WIFI_DEVICES], alias = "wifi.nearby")
    ]
)
class AndroidPermissionBrokerPlugin : Plugin() {
    @PluginMethod
    fun check(call: PluginCall) {
        val id = call.getString("id") ?: return call.reject("Missing permission id")
        call.resolve(diagnostics(id))
    }

    @PluginMethod
    fun request(call: PluginCall) {
        val id = call.getString("id") ?: return call.reject("Missing permission id")
        when (id) {
            "overlay", "exactAlarm", "file.manage", "autostart", "backgroundPopup", "batteryUnrestricted" -> {
                openSettings(call)
                call.resolve(diagnostics(id))
            }
            else -> requestPermissionForAlias(id, call, "permissionCallback")
        }
    }

    @PermissionCallback
    private fun permissionCallback(call: PluginCall) {
        val id = call.getString("id") ?: return call.reject("Missing permission id")
        call.resolve(diagnostics(id))
    }

    @PluginMethod
    fun openSettings(call: PluginCall) {
        val id = call.getString("id") ?: return call.reject("Missing permission id")
        val context = bridge.activity ?: context
        val intent = when (id) {
            "overlay" -> OemSettingsRouter.overlayIntent(context)
            "exactAlarm" -> OemSettingsRouter.exactAlarmIntent(context)
            "file.manage" -> OemSettingsRouter.allFilesIntent(context)
            "notification" -> OemSettingsRouter.notificationIntent(context)
            "autostart", "backgroundPopup", "batteryUnrestricted" -> null
            else -> OemSettingsRouter.appDetailsIntent(context)
        }

        try {
            if (intent != null) context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            else OemSettingsRouter.open(context, id)
            call.resolve()
        } catch (error: Throwable) {
            call.reject("Unable to open settings for $id: ${error.message}")
        }
    }

    @PluginMethod
    fun test(call: PluginCall) {
        val id = call.getString("id") ?: return call.reject("Missing permission id")
        val out = JSObject()
        when (id) {
            "notification" -> {
                if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    out.put("ok", false)
                    out.put("message", "Notifications are disabled.")
                } else {
                    val notification = NotificationCompat.Builder(context, "permission_lab")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Permission Lab")
                        .setContentText("Test notification from Permission Lab")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build()
                    try {
                        NotificationManagerCompat.from(context).notify(1001, notification)
                        out.put("ok", true)
                        out.put("message", "Notification submitted. Create a channel before using this in production.")
                    } catch (e: SecurityException) {
                        out.put("ok", false)
                        out.put("message", e.message ?: "Notification SecurityException")
                    }
                }
            }
            "batteryUnrestricted" -> {
                out.put("ok", isIgnoringBatteryOptimizations())
                out.put("message", if (isIgnoringBatteryOptimizations()) "Battery optimization ignored." else "Battery optimization still active.")
            }
            else -> {
                val state = diagnostics(id).getString("state")
                out.put("ok", state == "granted")
                out.put("message", "Capability smoke test placeholder. Permission state is $state. Add a real API call for production.")
            }
        }
        call.resolve(out)
    }

    private fun diagnostics(id: String): JSObject {
        val out = JSObject()
        out.put("id", id)
        out.put("state", resolveState(id))
        out.put("platform", "android")
        out.put("manufacturer", Build.MANUFACTURER)
        out.put("model", Build.MODEL)
        out.put("sdkInt", Build.VERSION.SDK_INT)
        out.put("targetSdk", context.applicationInfo.targetSdkVersion)
        out.put("packageName", context.packageName)
        out.put("details", JSObject().apply {
            put("isNotificationEnabled", NotificationManagerCompat.from(context).areNotificationsEnabled())
            put("canDrawOverlays", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(context) else true)
            put("canScheduleExactAlarms", canScheduleExactAlarms())
            put("isExternalStorageManager", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager() else false)
            put("isIgnoringBatteryOptimizations", isIgnoringBatteryOptimizations())
        })
        return out
    }

    private fun resolveState(id: String): String {
        return when (id) {
            "overlay" -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)) "granted" else "manualRequired"
            "exactAlarm" -> if (canScheduleExactAlarms()) "granted" else "manualRequired"
            "file.manage" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) "granted" else "manualRequired"
            "autostart", "backgroundPopup" -> "manualRequired"
            "batteryUnrestricted" -> if (isIgnoringBatteryOptimizations()) "granted" else "manualRequired"
            "notification" -> resolveNotificationState()
            else -> resolveRuntimeAlias(id)
        }
    }

    private fun resolveRuntimeAlias(alias: String): String {
        val state = getPermissionState(alias)
        return when (state) {
            PermissionState.GRANTED -> "granted"
            PermissionState.DENIED -> "denied"
            PermissionState.PROMPT -> "promptable"
            else -> "unknown"
        }
    }

    private fun resolveNotificationState(): String {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return "denied"
        if (Build.VERSION.SDK_INT < 33) return "granted"
        return if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            "granted"
        } else {
            "promptable"
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}
