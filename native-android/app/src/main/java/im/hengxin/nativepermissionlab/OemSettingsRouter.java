package im.hengxin.nativepermissionlab;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

final class OemSettingsRouter {
    private OemSettingsRouter() {
    }

    static Intent appDetailsIntent(Context context) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
    }

    static Intent autostartIntent(Context context) {
        String manufacturer = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.toLowerCase();
        ComponentName[] candidates;

        if (manufacturer.contains("xiaomi") || manufacturer.contains("redmi")) {
            candidates = new ComponentName[] {
                new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
                new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            };
        } else if (manufacturer.contains("oppo") || manufacturer.contains("oneplus") || manufacturer.contains("realme")) {
            candidates = new ComponentName[] {
                new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"),
                new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")
            };
        } else if (manufacturer.contains("vivo") || manufacturer.contains("iqoo")) {
            candidates = new ComponentName[] {
                new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"),
                new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
            };
        } else if (manufacturer.contains("huawei") || manufacturer.contains("honor")) {
            candidates = new ComponentName[] {
                new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
            };
        } else {
            candidates = new ComponentName[0];
        }

        for (ComponentName component : candidates) {
            Intent intent = new Intent().setComponent(component);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                return intent;
            }
        }
        return appDetailsIntent(context);
    }

    static Intent backgroundPopupIntent(Context context) {
        String manufacturer = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.toLowerCase();
        if (manufacturer.contains("xiaomi") || manufacturer.contains("redmi")) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR")
                .setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                .putExtra("extra_pkgname", context.getPackageName());
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                return intent;
            }
        }
        return appDetailsIntent(context);
    }

    static Intent batteryOptimizationIntent(Context context) {
        return new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
    }

    static void open(Context context, Intent intent) {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
