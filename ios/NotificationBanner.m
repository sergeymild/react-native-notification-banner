#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(NotificationBanner, NSObject)

RCT_EXTERN_METHOD(show:(NSDictionary*)params
                  onPress:(RCTResponseSenderBlock)onPress)

RCT_EXTERN_METHOD(configure:(NSDictionary*)params)

@end
