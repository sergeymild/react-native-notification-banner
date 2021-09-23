# react-native-notification-banner

No

## Installation

```sh
"react-native-notification-banner": "sergeymild/react-native-notification-banner"
```

## Usage

```js
import NotificationBanner from "react-native-notification-banner";

// initial configuration
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

NotificationBanner.configure(ConfigurationParams);

// ...
interface Params {
  title?: string;
  subtitle?: string;
  style?: 'success' | 'error' | 'info';
  duration?: number;
  borderRadius?: number;
  elevation?: number;
  onPress?: () => void
}
NotificationBanner.show(params);
```
