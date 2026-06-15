import { Capacitor, registerPlugin } from '@capacitor/core';
import type { CapabilityTestResult, PermissionBroker, PermissionDiagnostics, PermissionId } from './types';
import { WebPermissionBroker } from './webPermissionBroker';

interface AndroidPermissionBrokerPlugin {
  check(options: { id: PermissionId }): Promise<PermissionDiagnostics>;
  request(options: { id: PermissionId }): Promise<PermissionDiagnostics>;
  openSettings(options: { id: PermissionId }): Promise<void>;
  test(options: { id: PermissionId }): Promise<CapabilityTestResult>;
}

const AndroidPermissionBroker = registerPlugin<AndroidPermissionBrokerPlugin>('AndroidPermissionBroker');

export class CapacitorPermissionBroker implements PermissionBroker {
  private readonly web = new WebPermissionBroker();

  private get isNativeAndroid(): boolean {
    return Capacitor.isNativePlatform() && Capacitor.getPlatform() === 'android';
  }

  check(id: PermissionId): Promise<PermissionDiagnostics> {
    return this.isNativeAndroid ? AndroidPermissionBroker.check({ id }) : this.web.check(id);
  }

  request(id: PermissionId): Promise<PermissionDiagnostics> {
    return this.isNativeAndroid ? AndroidPermissionBroker.request({ id }) : this.web.request(id);
  }

  openSettings(id: PermissionId): Promise<void> {
    return this.isNativeAndroid ? AndroidPermissionBroker.openSettings({ id }) : this.web.openSettings(id);
  }

  test(id: PermissionId): Promise<CapabilityTestResult> {
    return this.isNativeAndroid ? AndroidPermissionBroker.test({ id }) : this.web.test(id);
  }
}
