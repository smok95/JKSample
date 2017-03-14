package com.example.smok95.fcmclient;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by smok95 on 14/03/2017.
 */

public class MyFirebaseInstanceIDService
        extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";
    @Override
    public void onTokenRefresh(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed toekn: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }
}
