import {
  Image,
  ImageSourcePropType,
  NativeModules,
  processColor,
} from 'react-native';

interface Params {
  title?: string;
  message?: string;
  style?: 'success' | 'error' | 'info';
  duration?: number;
  icon?: number;
  onPress?: () => void;
}

interface Style {
  backgroundColor?: string;
  titleColor?: string;
  messageColor?: string;
  icon?: ImageSourcePropType;
  titleFont?: {
    size?: number;
    family?: string;
  };
}

interface ConfigurationParams {
  cornerRadius?: number;
  elevation?: number;
  margin?: number;
  padding?: number;

  readonly shadow?: {
    offset: { width: number; height: number };
    color: string;
    radius: number;
    opacity: number;
  };

  readonly error?: Style;
  readonly success?: Style;
}

const noop = () => {};
export class NotificationBanner {
  private constructor() {}

  static configure(params: ConfigurationParams) {
    NativeModules.NotificationBanner.configure({
      ...params,
      shadow: !params.shadow
        ? undefined
        : {
            ...params.shadow,
            color: processColor(params.shadow.color),
          },
      error: !params.error
        ? undefined
        : {
            ...params.error,
            backgroundColor: !params.error.backgroundColor
              ? undefined
              : processColor(params.error.backgroundColor),
            titleColor: !params.error.titleColor
              ? undefined
              : processColor(params.error.titleColor),
            messageColor: !params.error.messageColor
              ? undefined
              : processColor(params.error.messageColor),
            icon: !params.error.icon
              ? undefined
              : Image.resolveAssetSource(params.error.icon).uri,
          },

      success: !params.success
        ? undefined
        : {
            ...params.success,
            backgroundColor: !params.success.backgroundColor
              ? undefined
              : processColor(params.success.backgroundColor),
            titleColor: !params.success.titleColor
              ? undefined
              : processColor(params.success.titleColor),
            messageColor: !params.success.messageColor
              ? undefined
              : processColor(params.success.messageColor),
            icon: !params.success.icon
              ? undefined
              : Image.resolveAssetSource(params.success.icon).uri,
          },
    });
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
