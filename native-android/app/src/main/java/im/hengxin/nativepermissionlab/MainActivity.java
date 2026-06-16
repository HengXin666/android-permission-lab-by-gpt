package im.hengxin.nativepermissionlab;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MainActivity extends Activity {
    private static final String CHANNEL_ID = "permission_lab";
    private LinearLayout list;

    private static final class PermissionItem {
        final String id;
        final String title;
        final String note;
        final List<IPermission> permissions;
        final Runnable manualSettings;

        PermissionItem(String id, String title, String note, List<IPermission> permissions, Runnable manualSettings) {
            this.id = id;
            this.title = title;
            this.note = note;
            this.permissions = permissions;
            this.manualSettings = manualSettings;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        ScrollView scrollView = new ScrollView(this);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(dp(16), dp(16), dp(16), dp(32));
        scrollView.addView(list);
        setContentView(scrollView);

        addHeader();
        for (PermissionItem item : buildItems()) {
            addCard(item);
        }
    }

    private void addHeader() {
        TextView title = text("Native Android Permission Lab", 24, true);
        list.addView(title);

        TextView desc = text(
            "基于 getActivity/XXPermissions 的原生 Android 权限实验程序。标准运行时权限走 XXPermissions；国产 ROM 自启动、后台弹出、电池限制走手动设置页。\n\n" +
                "Device: " + Build.MANUFACTURER + " " + Build.MODEL + " / Android " + Build.VERSION.RELEASE + " / API " + Build.VERSION.SDK_INT,
            14,
            false
        );
        desc.setPadding(0, dp(8), 0, dp(16));
        list.addView(desc);
    }

    private List<PermissionItem> buildItems() {
        List<PermissionItem> items = new ArrayList<>();
        items.add(new PermissionItem("camera", "Camera", "扫码、拍照、视频预览。", one(PermissionLists.getCameraPermission()), null));
        items.add(new PermissionItem("microphone", "Microphone", "录音、语音通话。", one(PermissionLists.getRecordAudioPermission()), null));
        items.add(new PermissionItem("location.foreground", "Foreground location", "前台定位。", many(
            PermissionLists.getAccessCoarseLocationPermission(),
            PermissionLists.getAccessFineLocationPermission()
        ), null));
        items.add(new PermissionItem("location.background", "Background location", "后台定位需要和前台定位分开处理。", one(PermissionLists.getAccessBackgroundLocationPermission()), null));
        items.add(new PermissionItem("notification", "Post notifications", "Android 13+ 通知运行时权限。", one(PermissionLists.getPostNotificationsPermission()), null));
        items.add(new PermissionItem("photo.read", "Photo / media read", "Android 13+ 图片、视频、音频和 Android 14 部分访问。", many(
            PermissionLists.getReadMediaImagesPermission(),
            PermissionLists.getReadMediaVideoPermission(),
            PermissionLists.getReadMediaAudioPermission(),
            PermissionLists.getReadMediaVisualUserSelectedPermission()
        ), null));
        items.add(new PermissionItem("bluetooth", "Bluetooth", "Android 12+ 蓝牙扫描/连接。", many(
            PermissionLists.getBluetoothScanPermission(),
            PermissionLists.getBluetoothConnectPermission()
        ), null));
        items.add(new PermissionItem("wifi.nearby", "Nearby Wi-Fi devices", "Android 13+ 附近 Wi-Fi 设备。", one(PermissionLists.getNearbyWifiDevicesPermission()), null));
        items.add(new PermissionItem("overlay", "Draw over other apps", "悬浮窗/覆盖其他应用，属于 special access。", one(PermissionLists.getSystemAlertWindowPermission()), null));
        items.add(new PermissionItem("exactAlarm", "Exact alarm", "Android 12+ 精确闹钟 special access。", one(PermissionLists.getScheduleExactAlarmPermission()), null));
        items.add(new PermissionItem("file.manage", "Manage all files", "所有文件访问权限，谨慎使用。", one(PermissionLists.getManageExternalStoragePermission()), null));
        items.add(new PermissionItem("batteryUnrestricted", "Ignore battery optimization", "忽略电池优化。标准入口 + 厂商入口都可能需要。", one(PermissionLists.getRequestIgnoreBatteryOptimizationsPermission()), () -> openManual("battery", OemSettingsRouter.batteryOptimizationIntent(this))));
        items.add(new PermissionItem("installedApps", "Get installed apps", "部分国产系统会把读取应用列表作为独立权限。", one(PermissionLists.getGetInstalledAppsPermission()), null));
        items.add(new PermissionItem("autostart", "OEM autostart", "小米/OPPO/vivo/华为等常见手动开关；没有标准 Android runtime prompt。", Collections.emptyList(), () -> openManual("autostart", OemSettingsRouter.autostartIntent(this))));
        items.add(new PermissionItem("backgroundPopup", "OEM background popup", "小米/HyperOS 等 ROM 可能有后台弹出页面权限。", Collections.emptyList(), () -> openManual("backgroundPopup", OemSettingsRouter.backgroundPopupIntent(this))));
        return items;
    }

    private void addCard(PermissionItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackgroundColor(Color.rgb(246, 247, 249));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(0, 0, 0, dp(12));
        list.addView(card, cardLp);

        card.addView(text(item.title + "  [" + item.id + "]", 18, true));
        card.addView(text(item.note, 13, false));
        TextView state = text("State: " + stateOf(item), 13, false);
        state.setPadding(0, dp(8), 0, dp(8));
        card.addView(state);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setGravity(Gravity.START);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(buttons);

        Button check = button("Check");
        check.setOnClickListener(v -> state.setText("State: " + stateOf(item)));
        buttons.addView(check);

        Button request = button(item.permissions.isEmpty() ? "Manual" : "Request");
        request.setOnClickListener(v -> {
            if (item.permissions.isEmpty()) {
                if (item.manualSettings != null) item.manualSettings.run();
                return;
            }
            XXPermissions.with(this)
                .permissions(item.permissions)
                .request((grantedList, deniedList) -> {
                    boolean ok = deniedList.isEmpty();
                    Toast.makeText(this, ok ? "Granted: " + grantedList.size() : "Denied: " + deniedList.size(), Toast.LENGTH_SHORT).show();
                    state.setText("State: " + stateOf(item));
                });
        });
        buttons.addView(request);

        Button settings = button("Settings");
        settings.setOnClickListener(v -> {
            if (item.manualSettings != null) {
                item.manualSettings.run();
            } else if (!item.permissions.isEmpty()) {
                XXPermissions.startPermissionActivity(this, item.permissions);
            } else {
                OemSettingsRouter.open(this, OemSettingsRouter.appDetailsIntent(this));
            }
        });
        buttons.addView(settings);

        Button test = button("Real test");
        test.setOnClickListener(v -> runSmokeTest(item));
        buttons.addView(test);
    }

    private String stateOf(PermissionItem item) {
        if (item.permissions.isEmpty()) {
            return "manualRequired / " + Build.MANUFACTURER;
        }
        boolean granted = XXPermissions.isGrantedPermissions(this, item.permissions);
        if ("batteryUnrestricted".equals(item.id)) {
            return granted ? "granted" : "manualRequired";
        }
        return granted ? "granted" : "denied-or-promptable";
    }

    private void runSmokeTest(PermissionItem item) {
        if ("notification".equals(item.id)) {
            if (!XXPermissions.isGrantedPermissions(this, item.permissions)) {
                Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
            Notification notification = builder
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Permission Lab")
                .setContentText("Native notification smoke test")
                .build();
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(1001, notification);
            Toast.makeText(this, "Notification submitted", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("batteryUnrestricted".equals(item.id)) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean ignoring = pm.isIgnoringBatteryOptimizations(getPackageName());
            Toast.makeText(this, ignoring ? "Battery optimization ignored" : "Battery optimization still active", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, stateOf(item), Toast.LENGTH_SHORT).show();
    }

    private void openManual(String id, Intent intent) {
        try {
            OemSettingsRouter.open(this, intent);
        } catch (Throwable error) {
            Toast.makeText(this, "Unable to open " + id + ": " + error.getMessage(), Toast.LENGTH_LONG).show();
            OemSettingsRouter.open(this, OemSettingsRouter.appDetailsIntent(this));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Permission Lab", NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
    }

    private TextView text(String value, int sp, boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(value);
        textView.setTextSize(sp);
        textView.setTextColor(Color.rgb(23, 32, 51));
        if (bold) textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);
        return textView;
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(8), 0);
        button.setLayoutParams(lp);
        return button;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static List<IPermission> one(IPermission permission) {
        return Collections.singletonList(permission);
    }

    private static List<IPermission> many(IPermission... permissions) {
        return Arrays.asList(permissions);
    }
}
