# react-native-notification-banner

No

## Installation

```sh
"react-native-notification-banner": "sergeymild/react-native-notification-banner"
```

## Usage

```js
import NotificationBanner from "react-native-notification-banner";

// ...
interface Params {
  title: string;
  subtitle?: string;
  style?: 'success' | 'error' | 'info';
  duration?: number;
  borderRadius?: number;
  elevation?: number;
}
NotificationBanner.show(params);
```
