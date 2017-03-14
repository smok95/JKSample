package com.example.smok95.fcmclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // cio_xyHZeWY:APA91bGwiPmy0D91Lm9IqJuU0E-7Mzsjsvre8_vSrxcKKXrUeOgAgZ3WRJYbiSQxzLL7CjLXhG6ecOUPOhq-aPCtuMNqPEaLwdKIuHAN9U4oAUWMoi1PVYYxzquf91dZTfeWNYZZxawicio_xyHZeWY:APA91bGwiPmy0D91Lm9IqJuU0E-7Mzsjsvre8_vSrxcKKXrUeOgAgZ3WRJYbiSQxzLL7CjLXhG6ecOUPOhq-aPCtuMNqPEaLwdKIuHAN9U4oAUWMoi1PVYYxzquf91dZTfeWNYZZxawi
        String token = FirebaseInstanceId.getInstance().getToken();


        FirebaseMessaging.getInstance().subscribeToTopic("news");

        Log.d(TAG, "token = " + token);
    }
}
