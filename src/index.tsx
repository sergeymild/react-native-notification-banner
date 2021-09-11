import { NativeModules } from 'react-native';

type NotificationBannerType = {
  multiply(a: number, b: number): Promise<number>;
};

const { NotificationBanner } = NativeModules;

export default NotificationBanner as NotificationBannerType;
