
package com.reactlibrary;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;


public class RNNearbyModule extends ReactContextBaseJavaModule implements LifecycleEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "RNNearby";

    private final ReactApplicationContext reactContext;

    private GoogleApiClient mGoogleApiClient;

    private MessageListener mMessageListener = new MessageListener() {
        /**
         * This method emits Android's nearby messages to react-native's javascript.
         * All events are name spaced by default like: `RNNearby_FOUND`.
         *
         * @param eventType Event type (eg. onFound).
         * @param message Message object.
         */
        private void emitToJS(String eventType, Message message) {
            WritableMap eventData = Arguments.createMap();
            eventData.putString("content", new String(message.getContent()));
            eventData.putString("namespace", new String(message.getNamespace()));
            eventData.putString("type", new String(message.getType()));

            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(TAG.concat("_".concat(eventType.toUpperCase())), eventData);
        }

        @Override
        public void onFound(Message message) {
            Log.d(TAG, "Found message: " + new String(message.getContent()));

            emitToJS("FOUND", message);
        }

        @Override
        public void onLost(Message message) {
            Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));

            emitToJS("LOST", message);
        }

        @Override
        public void onBleSignalChanged(Message message, BleSignal bleSignal) {
            Log.d(TAG, "onBleSignalChanged");

            emitToJS("SIGNAL_CHANGE", message);
        }

        @Override
        public void onDistanceChanged(Message message, Distance distance) {
            Log.d(TAG, "onDistanceChanged");

            emitToJS("DISTANCE_CHANGE", message);
        }
    };

    public RNNearbyModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);

        Log.d(TAG, "Starting module");
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void initialize() {
        Log.d(TAG, "Initializing module");

        if(!checkPermissions()) {
            return;
        }

        if(!checkAndroidVersion()) {
            Log.w(TAG, "Android version is not compatible, ICS+ required");
            return;
        }

        initGmsClient();
    }

    @Override
    public void onHostResume() {
        Log.d(TAG, "Resuming Nearby subscriptions");
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, "Pausing Nearby subscriptions");
        unsubscribe();
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "Stopping Nearby subscriptions");
        unsubscribe();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Gms connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Gms connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "Gms connection failure");

        Log.d(TAG, connectionResult.toString());
        if(connectionResult.hasResolution()) {
            Log.d(TAG, "Connection resolution found");
            try {
                PendingIntent pendingIntent = connectionResult.getResolution();
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        } else {
            Log.d(TAG, "No connection resolution");
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions granted");

            return true;
        }

        Log.w(TAG, "Permissions required");
        return false;
    }

    private boolean checkAndroidVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    private void initGmsClient() {
        Log.d(TAG, "Initializing Google Api Client");
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getReactApplicationContext())
                    .addApi(Nearby.MESSAGES_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
     }

    @ReactMethod
    private void subscribe() {
        Log.d(TAG, "Subscribing");

        if (!this.isConnected()) return;

        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build();
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) {
                            Log.d(TAG, "Subscribe success!");
                        } else {
                            Log.e(TAG, "Subscribe failure: " + NearbyMessagesStatusCodes.getStatusCodeString(status.getStatusCode()));
                        }
                    }
                });
    }

    @ReactMethod
    private void unsubscribe() {
        Log.d(TAG, "Unsubscribing");
        if(this.isConnected()) {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
        }
    }
}