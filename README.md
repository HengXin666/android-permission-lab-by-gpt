# android-permission-lab-by-gpt

A modern Android/Web permission lab for React + Capacitor apps.

目标：把业务层从 Android 原生权限 API 中隔离出来，用一套 TypeScript `PermissionBroker` 同时覆盖：

- Web browser permissions
- Android runtime permissions
- Android special access permissions
- China ROM / OEM manual permissions, especially Xiaomi / HyperOS style settings
- Stable debug and reproducible permission reset flows

> This is an example/lab project, not a drop-in production SDK. The OEM settings pages are intentionally best-effort because vendors change private Activity names across ROM versions.

## Repo goals

1. Business code never calls native Android permission APIs directly.
2. All permission checks and requests go through `PermissionBroker`.
3. Every permission has a real capability test, not just a `granted` flag.
4. Development can reproduce permission states with ADB scripts.
5. OEM permissions are modeled explicitly as `manualRequired`, not hidden inside runtime permissions.

## Quick start

```bash
npm install
npm run dev
```

Android build path:

```bash
npm install
npm run build
npx cap sync android
npx cap open android
```

The repository includes an Android plugin skeleton under `android/app/src/main/java/im/hengxin/permissionlab` for reference. If you regenerate the Android folder with Capacitor, copy the plugin sources back into the generated app module.

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

- `src/permissions/types.ts`: cross-platform permission model
- `src/permissions/webPermissionBroker.ts`: browser implementation
- `src/permissions/capacitorPermissionBroker.ts`: Capacitor bridge
- `android/app/src/main/java/im/hengxin/permissionlab/AndroidPermissionBrokerPlugin.kt`: Android plugin skeleton
- `android/app/src/main/java/im/hengxin/permissionlab/OemSettingsRouter.kt`: OEM settings intents
- `scripts/adb-reset-permissions.sh`: reproducible debug reset
- `.agent/skills/android-permission-broker/SKILL.md`: AI-agent rule set for future changes
