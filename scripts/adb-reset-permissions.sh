#!/usr/bin/env bash
set -euo pipefail

PKG="${1:-im.hengxin.permissionlab}"

runtime_permissions=(
  android.permission.CAMERA
  android.permission.RECORD_AUDIO
  android.permission.ACCESS_FINE_LOCATION
  android.permission.ACCESS_COARSE_LOCATION
  android.permission.ACCESS_BACKGROUND_LOCATION
  android.permission.POST_NOTIFICATIONS
  android.permission.READ_EXTERNAL_STORAGE
  android.permission.READ_MEDIA_IMAGES
  android.permission.READ_MEDIA_VIDEO
  android.permission.BLUETOOTH_SCAN
  android.permission.BLUETOOTH_CONNECT
  android.permission.NEARBY_WIFI_DEVICES
)

for perm in "${runtime_permissions[@]}"; do
  echo "Reset $perm"
  adb shell pm revoke "$PKG" "$perm" >/dev/null 2>&1 || true
  adb shell pm clear-permission-flags "$PKG" "$perm" user-set user-fixed >/dev/null 2>&1 || true
  adb shell pm clear-permission-flags "$PKG" "$perm" user-sensitive-when-granted user-sensitive-when-denied >/dev/null 2>&1 || true
done

echo "\nPackage permissions:"
adb shell dumpsys package "$PKG" | sed -n '/runtime permissions:/,/install permissions:/p' || true

echo "\nAppOps:"
adb shell appops get "$PKG" || true
