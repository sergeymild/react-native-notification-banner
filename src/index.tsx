import { NativeModules } from 'react-native';

interface Params {
  title?: string;
  subtitle?: string;
  style?: 'success' | 'error' | 'info';
  duration?: number;
  borderRadius?: number;
  elevation?: number;
  onPress?: () => void;
}

const noop = () => {};
export class NotificationBanner {
  private constructor() {}

  static show(params: Params) {
    const onPress = params.onPress ? params.onPress : noop;
    NativeModules.NotificationBanner.show(params, onPress);
  }
}
