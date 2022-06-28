import * as React from 'react';

import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { NotificationBanner } from 'react-native-notification-banner';
import { useEffect } from 'react';

export default function App() {
  useEffect(() => {
    NotificationBanner.configure({
      cornerRadius: 1000,

      elevation: 20,
      padding: 20,

      shadow: {
        color: '#9C9C9C',
        offset: { width: 0, height: 8 },
        opacity: 0.16,
        radius: 20,
      },

      success: {
        backgroundColor: '#ffffff',
        titleColor: '#1f1f1f',
        messageColor: '#775656',
        titleFont: {
          size: 15,
          textAlign: 'center'
        },
      },

      error: {
        backgroundColor: 'orange',
        titleColor: 'yellow',
        titleFont: {
          size: 12,
          textAlign: 'left'
        },
      },
    });
  });

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={{ marginTop: 100 }}
        onPress={() => {
          NotificationBanner.show({
            title: 'error title',
            message: 'error subtitle',
            style: 'error',
            duration: 6000,
            onPress: () => {
              console.log('error press');
            },
          });
        }}
      >
        <Text>Error</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={{ marginTop: 100 }}
        onPress={() => {
          NotificationBanner.show({
            title: 'Complain success Complain success',
            style: 'success',
            duration: 6000,
            onPress: () => {
              console.log('Success press');
            },
          });
        }}
      >
        <Text>Success</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    flexDirection: 'column',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
