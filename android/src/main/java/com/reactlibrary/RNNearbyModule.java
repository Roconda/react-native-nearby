
package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;


public class RNNearbyModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private static final String TAG = "RNNearby";

    private final ReactApplicationContext reactContext;

    public RNNearbyModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        Log.d(TAG, "Starting module");
    }

    @Override
    public String getName() {
        return "RNNearby";
    }

    @Override
    public void onHostResume() {
        Log.d(TAG, "Resuming Nearby subscriptions");
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, "Pausing Nearby subscriptions");
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "Stopping Nearby subscriptions");
    }
}