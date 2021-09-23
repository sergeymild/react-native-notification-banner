import { Image, ImageSourcePropType, NativeModules } from 'react-native';

interface Params {
  title?: string;
  subtitle?: string;
  style?: 'success' | 'error' | 'info';
  duration?: number;
  icon?: number;
  onPress?: () => void;
}

interface ConfigurationParams {
  cornerRadius?: number;
  errorIcon?: ImageSourcePropType;
  elevation?: number;
  successBackgroundColor?: string;
  errorBackgroundColor?: string;
  successTitleColor?: string;
  successSubtitleColor?: string;
  errorTitleColor?: string;
  errorSubtitleColor?: string;
}

const noop = () => {};
export class NotificationBanner {
  private constructor() {}

  static configure(params: ConfigurationParams) {
    //@ts-ignore
    params.errorIcon = params.errorIcon
      ? Image.resolveAssetSource(params.errorIcon).uri
      : undefined;
    NativeModules.NotificationBanner.configure(params);
  }

  static show(params: Params) {
    const onPress = params.onPress ? params.onPress : noop;
    //@ts-ignore
    params.icon = params.icon
      ? Image.resolveAssetSource(params.icon).uri
      : undefined;
    NativeModules.NotificationBanner.show(params, onPress);
  }
}
