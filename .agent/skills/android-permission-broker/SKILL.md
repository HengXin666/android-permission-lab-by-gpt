---
name: android-permission-broker
description: Use when adding, reviewing, or debugging Web/Android permission flows, especially Chinese OEM ROM behavior.
---

# Android Permission Broker skill

## Hard rules

1. Business UI must not call Android permission APIs directly.
2. All permission operations must go through the TypeScript `PermissionBroker`.
3. Every new permission must be added to `src/permissions/catalog.ts` and `docs/permission-matrix.md`.
4. Never treat `permission granted` as `capability works`; add a real capability test.
5. Model OEM permissions explicitly as `manualRequired` when no runtime prompt exists.
6. Keep Web, Android runtime, Android special access, and OEM manual settings separate.

## Required implementation checklist

For every new permission:

- PermissionId
- Web behavior
- Android manifest permission
- Runtime request path
- Special access settings path, if any
- OEM notes for Xiaomi/OPPO/vivo/Huawei where relevant
- ADB reset recipe
- Real capability test
- User-facing explanation
- Diagnostics field

## China ROM notes

- Xiaomi / HyperOS may expose autostart, background popup, notification, and app-list permissions separately.
- OPPO / vivo / Huawei often have private autostart and battery managers.
- Private settings Activities can break after ROM updates; always provide fallback to app details settings.
- Background work must be tested after lock screen, clear recents, reboot, and long idle.
