export type PermissionId =
  | 'camera'
  | 'microphone'
  | 'location.foreground'
  | 'location.background'
  | 'notification'
  | 'photo.read'
  | 'file.manage'
  | 'bluetooth'
  | 'wifi.nearby'
  | 'overlay'
  | 'exactAlarm'
  | 'autostart'
  | 'backgroundPopup'
  | 'batteryUnrestricted';

export type PermissionState =
  | 'granted'
  | 'denied'
  | 'promptable'
  | 'blocked'
  | 'limited'
  | 'unsupported'
  | 'manualRequired'
  | 'unknown';

export type PermissionCategory = 'runtime' | 'specialAccess' | 'oemManual' | 'webOnly';

export interface PermissionSpec {
  id: PermissionId;
  title: string;
  category: PermissionCategory;
  description: string;
  androidPermissions?: string[];
  androidSpecialAccess?: string;
  webPermissionName?: PermissionName | string;
  oemNotes?: string;
}

export interface PermissionDiagnostics {
  id: PermissionId;
  state: PermissionState;
  platform: 'web' | 'android' | 'ios' | 'unknown';
  manufacturer?: string;
  model?: string;
  sdkInt?: number;
  targetSdk?: number;
  packageName?: string;
  details?: Record<string, unknown>;
}

export interface CapabilityTestResult {
  ok: boolean;
  message: string;
  details?: Record<string, unknown>;
}

export interface PermissionBroker {
  check(id: PermissionId): Promise<PermissionDiagnostics>;
  request(id: PermissionId): Promise<PermissionDiagnostics>;
  openSettings(id: PermissionId): Promise<void>;
  test(id: PermissionId): Promise<CapabilityTestResult>;
}
