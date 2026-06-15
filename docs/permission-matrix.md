# Permission matrix

| PermissionId | Web | Android runtime | Android special access | OEM / China ROM behavior | Real test |
|---|---|---|---|---|---|
| camera | getUserMedia video | CAMERA | - | Usually standard | Open camera stream |
| microphone | getUserMedia audio | RECORD_AUDIO | - | Usually standard | Open audio stream |
| location.foreground | geolocation | FINE/COARSE_LOCATION | - | May require location service enabled | Get current position |
| location.background | - | ACCESS_BACKGROUND_LOCATION | Settings-mediated on newer Android | Strongly affected by battery policy | Background tracking smoke test |
| notification | Notification API | POST_NOTIFICATIONS on Android 13+ | Notification settings | OEM notification categories/channels may block | Post local notification |
| photo.read | File picker | READ_MEDIA_* / READ_EXTERNAL_STORAGE | Photo picker recommended | Gallery apps may vary | Pick/read media |
| file.manage | - | - | MANAGE_EXTERNAL_STORAGE | Some ROMs hide/restrict entry | Check Environment.isExternalStorageManager |
| bluetooth | Web Bluetooth limited | BLUETOOTH_SCAN/CONNECT | - | Location toggle may still matter | Scan/connect smoke test |
| wifi.nearby | - | NEARBY_WIFI_DEVICES | - | Vendor behavior varies | Wi-Fi scan/discovery smoke test |
| overlay | - | - | SYSTEM_ALERT_WINDOW | Some ROMs add extra floating-window toggles | Show overlay |
| exactAlarm | - | - | SCHEDULE_EXACT_ALARM | Battery policy may still delay work | Schedule exact alarm |
| autostart | - | - | - | Manual vendor setting | Reboot and receive BOOT_COMPLETED |
| backgroundPopup | - | - | - | Xiaomi/HyperOS style manual switch | Try background UI launch only in allowed scenarios |
| batteryUnrestricted | - | - | REQUEST_IGNORE_BATTERY_OPTIMIZATIONS | OEM battery manager may override | Long-running background benchmark |
