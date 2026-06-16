# Native Android permission baseline

This repository now uses a real native Android demo app under `native-android/`.

The native app is intentionally based on the open-source `getActivity/XXPermissions` permission framework instead of a hand-written Capacitor Android shell.

## Why XXPermissions

- It is a public Android permission framework focused on modern Android permission compatibility.
- It has Chinese documentation and is closer to Chinese Android development practice than Google-only samples.
- It provides APIs for dangerous runtime permissions and many Android special-access permissions.
- The upstream README currently documents these dependencies:

```gradle
implementation 'com.github.getActivity:DeviceCompat:2.6'
implementation 'com.github.getActivity:XXPermissions:28.2'
```

Upstream:

- <https://github.com/getActivity/XXPermissions>

## Current repository layout

```text
native-android/                         Pure native Android permission demo
native-android/app/src/main/...         Java Activity + OEM settings router
src/                                    React/Web permission lab
android/                                Old Capacitor shell, kept only as reference for now
```

## CI behavior

The GitHub Actions workflow has two jobs:

1. `Web permission lab build`
   - Builds this repository's React/Vite permission lab.

2. `Native Android XXPermissions build`
   - Builds `native-android` with:

   ```bash
   gradle -p native-android :app:assembleDebug --stacktrace
   ```

   - Uploads the generated debug APK artifact.

## Native app behavior

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

Standard Android runtime and special-access permissions go through XXPermissions.

OEM-only capabilities such as autostart and background popup are treated as manual settings flows because they do not have a stable Android runtime permission prompt.

## Future migration plan

After the native app is stable, there are two possible directions:

1. Keep `native-android` as the real Android permission laboratory and use the React app only as the cross-platform model/UI reference.
2. Regenerate a standard Capacitor Android project locally, then bridge the proven native permission adapter back into Capacitor.

Do not hand-write a partial Capacitor Android project again; use the official Capacitor generator if that route is needed.
