#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <GNSMessages.h>

@interface RNNearby : RCTEventEmitter <RCTBridgeModule>
+ (instancetype)initWithApiKey:(nonnull NSString*)apiKey;
@end

