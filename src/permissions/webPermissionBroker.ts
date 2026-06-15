import { findPermissionSpec } from './catalog';
import type { CapabilityTestResult, PermissionBroker, PermissionDiagnostics, PermissionId, PermissionState } from './types';

function mapWebState(state: PermissionState | PermissionStatus['state'] | undefined): PermissionState {
  if (!state) return 'unknown';
  if (state === 'granted') return 'granted';
  if (state === 'denied') return 'denied';
  if (state === 'prompt') return 'promptable';
  return state as PermissionState;
}

async function queryWebPermission(id: PermissionId): Promise<PermissionState> {
  const spec = findPermissionSpec(id);
  const name = spec?.webPermissionName;

  if (!name || !('permissions' in navigator)) {
    return id === 'autostart' || id === 'backgroundPopup' || id === 'batteryUnrestricted'
      ? 'unsupported'
      : 'unknown';
  }

  try {
    const status = await navigator.permissions.query({ name: name as PermissionName });
    return mapWebState(status.state);
  } catch {
    return 'unknown';
  }
}

export class WebPermissionBroker implements PermissionBroker {
  async check(id: PermissionId): Promise<PermissionDiagnostics> {
    return {
      id,
      state: await queryWebPermission(id),
      platform: 'web',
      details: {
        secureContext: window.isSecureContext,
        userAgent: navigator.userAgent,
      },
    };
  }

  async request(id: PermissionId): Promise<PermissionDiagnostics> {
    switch (id) {
      case 'camera':
        await navigator.mediaDevices?.getUserMedia({ video: true });
        break;
      case 'microphone':
        await navigator.mediaDevices?.getUserMedia({ audio: true });
        break;
      case 'location.foreground':
        await new Promise<GeolocationPosition>((resolve, reject) => {
          navigator.geolocation.getCurrentPosition(resolve, reject, { timeout: 10_000 });
        });
        break;
      case 'notification':
        if ('Notification' in window) {
          await Notification.requestPermission();
        }
        break;
      default:
        // Many permissions have no Web prompt equivalent. Keep the state honest.
        break;
    }
    return this.check(id);
  }

  async openSettings(_id: PermissionId): Promise<void> {
    // Browsers do not expose a reliable deep link to site permission settings.
    alert('Open browser site settings manually. Browsers do not provide a stable settings deep link.');
  }

  async test(id: PermissionId): Promise<CapabilityTestResult> {
    try {
      switch (id) {
        case 'camera': {
          const stream = await navigator.mediaDevices.getUserMedia({ video: true });
          stream.getTracks().forEach((track) => track.stop());
          return { ok: true, message: 'Camera stream opened successfully.' };
        }
        case 'microphone': {
          const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
          stream.getTracks().forEach((track) => track.stop());
          return { ok: true, message: 'Microphone stream opened successfully.' };
        }
        case 'location.foreground': {
          const pos = await new Promise<GeolocationPosition>((resolve, reject) => {
            navigator.geolocation.getCurrentPosition(resolve, reject, { timeout: 10_000 });
          });
          return {
            ok: true,
            message: 'Foreground location returned a position.',
            details: { accuracy: pos.coords.accuracy },
          };
        }
        case 'notification': {
          if (!('Notification' in window)) return { ok: false, message: 'Notification API unsupported.' };
          if (Notification.permission !== 'granted') return { ok: false, message: 'Notification permission is not granted.' };
          new Notification('Permission Lab test notification');
          return { ok: true, message: 'Notification created.' };
        }
        default:
          return { ok: false, message: `No browser capability test for ${id}.` };
      }
    } catch (error) {
      return { ok: false, message: error instanceof Error ? error.message : String(error) };
    }
  }
}
