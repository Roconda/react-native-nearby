
package com.reactlibrary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;


public class RNNearbyModule extends ReactContextBaseJavaModule implements LifecycleEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "RNNearby";

    private final ReactApplicationContext reactContext;

    private GoogleApiClient mGoogleApiClient;

    public RNNearbyModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);

        Log.d(TAG, "Starting module");
    }

    @Override
    public String getName() {
        return "RNNearby";
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
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "Stopping Nearby subscriptions");
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

    private void initGmsClient() {
        Log.d(TAG, "Initializing Google Api Client");
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getReactApplicationContext())
                    .addApi(Nearby.MESSAGES_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        // Connect anyways
        mGoogleApiClient.connect();
     }

}