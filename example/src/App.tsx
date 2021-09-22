import * as React from 'react';

import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { NotificationBanner } from 'react-native-notification-banner';

export default function App() {
  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={{ marginTop: 100 }}
        onPress={() => {
          NotificationBanner.show({
            title: 'Some title',
            subtitle: 'Some su',
            style: 'success',
            duration: 6000,
            borderRadius: 14,
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
