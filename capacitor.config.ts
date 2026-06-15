import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'im.hengxin.permissionlab',
  appName: 'Permission Lab by GPT',
  webDir: 'dist',
  server: {
    androidScheme: 'https'
  }
};

export default config;
