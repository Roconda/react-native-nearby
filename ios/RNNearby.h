
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif
#import <GNSMessages.h>

@interface RNNearby : NSObject <RCTBridgeModule>
- (instancetype)initWithApiKey:(nonnull NSString*)apiKey;
@end
