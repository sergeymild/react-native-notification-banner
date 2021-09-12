import { NativeModules } from 'react-native';

interface Params {
  title?: string;
  subtitle?: string;
  style?: 'success' | 'error' | 'info';
  duration?: number;
  borderRadius?: number;
  elevation?: number;
}

type NotificationBannerType = {
  show(params: Params): void;
};

const { NotificationBanner } = NativeModules;

export default NotificationBanner as NotificationBannerType;
