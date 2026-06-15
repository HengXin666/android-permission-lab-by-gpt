import type { PermissionSpec } from './types';

export const permissionCatalog: PermissionSpec[] = [
  {
    id: 'camera',
    title: 'Camera',
    category: 'runtime',
    description: 'Use camera for scanning, taking photos, or video preview.',
    androidPermissions: ['android.permission.CAMERA'],
    webPermissionName: 'camera',
  },
  {
    id: 'microphone',
    title: 'Microphone',
    category: 'runtime',
    description: 'Record audio or participate in calls.',
    androidPermissions: ['android.permission.RECORD_AUDIO'],
    webPermissionName: 'microphone',
  },
  {
    id: 'location.foreground',
    title: 'Foreground location',
    category: 'runtime',
    description: 'Access location while the app is visible.',
    androidPermissions: [
      'android.permission.ACCESS_FINE_LOCATION',
      'android.permission.ACCESS_COARSE_LOCATION',
    ],
    webPermissionName: 'geolocation',
  },
  {
    id: 'location.background',
    title: 'Background location',
    category: 'runtime',
    description: 'Access location while the app is not visible. Requires a separate Android flow.',
    androidPermissions: ['android.permission.ACCESS_BACKGROUND_LOCATION'],
  },
  {
    id: 'notification',
    title: 'Notifications',
    category: 'runtime',
    description: 'Post local notifications. On Android 13+ this is a runtime permission.',
    androidPermissions: ['android.permission.POST_NOTIFICATIONS'],
    webPermissionName: 'notifications',
  },
  {
    id: 'photo.read',
    title: 'Photo / media read',
    category: 'runtime',
    description: 'Read user selected photos or media. Android versions differ a lot here.',
    androidPermissions: [
      'android.permission.READ_MEDIA_IMAGES',
      'android.permission.READ_MEDIA_VIDEO',
      'android.permission.READ_EXTERNAL_STORAGE',
    ],
  },
  {
    id: 'file.manage',
    title: 'Manage all files',
    category: 'specialAccess',
    description: 'All files access. Avoid unless the app genuinely needs file-manager behavior.',
    androidSpecialAccess: 'MANAGE_EXTERNAL_STORAGE',
  },
  {
    id: 'bluetooth',
    title: 'Bluetooth',
    category: 'runtime',
    description: 'Bluetooth scan/connect on Android 12+.',
    androidPermissions: [
      'android.permission.BLUETOOTH_SCAN',
      'android.permission.BLUETOOTH_CONNECT',
    ],
  },
  {
    id: 'wifi.nearby',
    title: 'Nearby Wi-Fi devices',
    category: 'runtime',
    description: 'Discover nearby Wi-Fi devices on Android 13+.',
    androidPermissions: ['android.permission.NEARBY_WIFI_DEVICES'],
  },
  {
    id: 'overlay',
    title: 'Draw over other apps',
    category: 'specialAccess',
    description: 'System alert window / floating window permission.',
    androidSpecialAccess: 'SYSTEM_ALERT_WINDOW',
  },
  {
    id: 'exactAlarm',
    title: 'Exact alarm',
    category: 'specialAccess',
    description: 'Schedule exact alarms on Android 12+.',
    androidSpecialAccess: 'SCHEDULE_EXACT_ALARM',
  },
  {
    id: 'autostart',
    title: 'OEM autostart',
    category: 'oemManual',
    description: 'Vendor-specific autostart switch. Common on Xiaomi, OPPO, vivo, Huawei.',
    oemNotes: 'Usually manual only; no standard Android runtime prompt exists.',
  },
  {
    id: 'backgroundPopup',
    title: 'OEM background popup',
    category: 'oemManual',
    description: 'Vendor-specific permission for launching UI from background.',
    oemNotes: 'Xiaomi/HyperOS exposes a separate background popup setting for some apps.',
  },
  {
    id: 'batteryUnrestricted',
    title: 'Ignore battery optimization',
    category: 'specialAccess',
    description: 'Allow long-running background work when genuinely needed.',
    androidSpecialAccess: 'REQUEST_IGNORE_BATTERY_OPTIMIZATIONS',
  },
];

export function findPermissionSpec(id: string): PermissionSpec | undefined {
  return permissionCatalog.find((item) => item.id === id);
}
