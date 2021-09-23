import * as React from 'react';

import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { NotificationBanner } from 'react-native-notification-banner';
import { useEffect } from 'react';

export default function App() {
  useEffect(() => {
    NotificationBanner.configure({
      successBackgroundColor: '#ffffff',
      successTitleColor: '#1f1f1f',
      successSubtitleColor: '#775656',
      cornerRadius: 1000,

      errorBackgroundColor: '#d95959',
      errorTitleColor: '#2c5181',
      errorSubtitleColor: '#961fa2',
      errorIcon: require('./assets/icError.png'),
      elevation: 5,
    });
  });

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={{ marginTop: 100 }}
        onPress={() => {
          NotificationBanner.show({
            title: 'Some title',
            subtitle: 'dsjdskdjs',
            style: 'success',
            duration: 6000,
            onPress: () => {
              console.log('sssss');
            },
          });
        }}
      >
        <Text>Press</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
