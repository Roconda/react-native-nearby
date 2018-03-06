#import "RNNearby.h"

NSString *_apiKey = nil;
GNSMessageManager *_msgManager = nil;
id<GNSSubscription> _msgSubscription = nil;

@implementation RNNearby
{
    bool hasListeners;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

+ (instancetype)initWithApiKey:(nonnull NSString*)apiKey {
    static RNNearby *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _apiKey = apiKey;
        sharedInstance = [[self alloc] init];
    });

    return sharedInstance;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[@"RNNearby_FOUND", @"RNNearby_LOST", @"RNNearby_SIGNAL_CHANGE", @"RNNearby_DISTANCE_CHANGE"];
}

- (void)emitToJS:(NSString*)eventType message:(GNSMessage*)message {
    NSString *receivedDataString = [[NSString alloc] initWithData:message.content encoding:NSUTF8StringEncoding];
    NSLog(@"RNNearby received data %@",receivedDataString);

    if (hasListeners) {
        [self sendEventWithName:eventType body:@{
                                                 @"content": receivedDataString,
                                                 @"namespace": message.messageNamespace,
                                                 @"type": message.type
                                                 }];
    }
}

- (void)startObserving {
    hasListeners = YES;
    NSLog(@"RNNearby start observing");
};
- (void)stopObserving {
    hasListeners = NO;
    NSLog(@"RNNearby stop observing");
};

RCT_EXPORT_METHOD(subscribe) {
    _msgManager = [[GNSMessageManager alloc] initWithAPIKey:_apiKey];
    _msgSubscription = [_msgManager subscriptionWithMessageFoundHandler:^(GNSMessage *message) {
        NSLog(@"RNNearby msg found!");

        [self emitToJS:@"RNNearby_FOUND" message:message];
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

RCT_EXPORT_METHOD(unsubscribe) {
    NSLog(@"RNNearby unsubscribe");
    _msgSubscription = nil;
}

@end
