#import "RNNearby.h"

NSString *_apiKey = nil;
GNSMessageManager *_msgManager = nil;
id<GNSSubscription> _msgSubscription = nil;

@implementation RNNearby

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

- (instancetype)initWithApiKey:(nonnull NSString*)apiKey {
    self = [super init];
    if (self != nil) {
        NSLog(@"RNNearby initiating");
        _apiKey = apiKey;
        NSLog(@"RNNearby apiKey: %@", apiKey);
        [self subscribe];
    }
    return self;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[@"RNNearby_FOUND", @"RNNearby_LOST"];
}

- (void)sendEventWithName:(NSString*)name body:(GNSMessage*)message {
    NSString *receivedDataString = [[NSString alloc] initWithData:message.content encoding:NSUTF8StringEncoding];
    NSLog(@"RNNearby received data %@",receivedDataString);
}

- (void)subscribe {
    NSLog(@"RNNearby subscribing");
    _msgManager = [[GNSMessageManager alloc] initWithAPIKey:_apiKey];
    _msgSubscription = [_msgManager subscriptionWithMessageFoundHandler:^(GNSMessage *message) {
        NSLog(@"RNNearby msg found!");

        [self sendEventWithName:@"RNNearby_FOUND" body:message];
    } messageLostHandler:^(GNSMessage *message) {
        NSLog(@"RNNearby msg lost!");
    } paramsBlock:^(GNSSubscriptionParams *params) {
        params.deviceTypesToDiscover = kGNSDeviceBLEBeacon;
        params.beaconStrategy = [GNSBeaconStrategy strategyWithParamsBlock:^(GNSBeaconStrategyParams *params) {
            params.includeIBeacons = NO;
            params.lowPowerPreferred = NO;
        }];
    }];
}

@end
