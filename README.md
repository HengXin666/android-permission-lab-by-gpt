# android-permission-lab-by-gpt

A modern Android/Web permission lab.

当前路线：

- `src/`：React/Web 权限模型与调试 UI。
- `native-android/`：原生 Android 权限实验 App，基于 `getActivity/XXPermissions`。
- `android/`：旧 Capacitor Android 壳，仅作为参考保留；不要继续在 CI 中构建它。

## Goals

目标：把业务层从 Android 原生权限 API 中隔离出来，用一套能力模型同时覆盖：

- Web browser permissions
- Android runtime permissions
- Android special access permissions
- China ROM / OEM manual permissions, especially Xiaomi / HyperOS style settings
- Stable debug and reproducible permission reset flows

> This is an example/lab project, not a drop-in production SDK. The OEM settings pages are intentionally best-effort because vendors change private Activity names across ROM versions.

## Quick start: Web lab

```bash
npm install
npm run dev
npm run build
```

## Quick start: Native Android lab

```bash
gradle -p native-android :app:assembleDebug
```

The debug APK will be generated under:

```text
native-android/app/build/outputs/apk/debug/
```

## Native Android baseline

`native-android/` uses:

```gradle
implementation 'com.github.getActivity:DeviceCompat:2.6'
implementation 'com.github.getActivity:XXPermissions:28.2'
```

The native app demonstrates:

- camera
- microphone
- foreground location
- background location
- post notifications
- photo/media read
- Bluetooth scan/connect
- nearby Wi-Fi devices
- overlay / floating window
- exact alarm
- manage all files
- ignore battery optimization
- get installed apps
- OEM autostart manual settings
- OEM background popup manual settings

Standard Android runtime and special-access permissions go through XXPermissions. OEM-only capabilities such as autostart and background popup are handled as manual settings flows.

## Permission model

```ts
PermissionId =
  | camera
  | microphone
  | location.foreground
  | location.background
  | notification
  | photo.read
  | file.manage
  | bluetooth
  | wifi.nearby
  | overlay
  | exactAlarm
  | autostart
  | backgroundPopup
  | batteryUnrestricted
```

Common states:

- `granted`: permission appears granted
- `denied`: denied but may still be requestable
- `promptable`: browser/native system may show a prompt
- `blocked`: user selected don't ask again or system cannot prompt
- `limited`: partial access, such as limited photos
- `manualRequired`: cannot be requested by runtime API; guide user to settings
- `unsupported`: current platform does not support this ability
- `unknown`: unable to determine

## Debug matrix

Recommended real devices:

- AOSP / Pixel Emulator: Android 11, 12, 13, 14, 15+
- Xiaomi / Redmi: MIUI and HyperOS
- OPPO / OnePlus: ColorOS
- vivo / iQOO: OriginOS
- Huawei / Honor: EMUI / HarmonyOS variants

Scenarios:

- first install
- deny once
- deny twice / don't ask again
- app upgrade
- targetSdk upgrade
- manual enable from settings
- lock screen + background + clear recent tasks
- reboot + autostart

## Files to inspect

- `native-android/app/src/main/java/im/hengxin/nativepermissionlab/MainActivity.java`: native Android permission lab UI
- `native-android/app/src/main/java/im/hengxin/nativepermissionlab/OemSettingsRouter.java`: China ROM manual settings intents
- `src/permissions/types.ts`: cross-platform permission model
- `src/permissions/webPermissionBroker.ts`: browser implementation
- `scripts/adb-reset-permissions.sh`: reproducible debug reset
- `.agent/skills/android-permission-broker/SKILL.md`: AI-agent rule set for future changes
