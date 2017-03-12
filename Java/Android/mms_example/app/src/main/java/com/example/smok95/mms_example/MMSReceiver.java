package com.example.smok95.mms_example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by smok95 on 12/03/2017.
 */

public class MMSReceiver extends BroadcastReceiver {
    private static final String ACTION_MMS_RECEIVED = "andorid.porvider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

    @Override
    public void onReceive(Context ctx, Intent intent){
        String action = intent.getAction();
        String type = intent.getType();

        if(!action.equals(ACTION_MMS_RECEIVED))
            return;

        MainActivity main = MainActivity.getInstance();
        if(main != null){
            main.addLog("action=" + action + ",type=" + type);
        }

        Bundle bundle = intent.getExtras();
        if(bundle == null)
            return;


    }
}
