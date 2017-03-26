package com.example.smok95.mms_example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by smok95 on 12/03/2017.
 */

public class MMSReceiver extends BroadcastReceiver {
    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

    private static final String TAG = "MMSReceiver";

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public void onReceive(Context ctx, Intent intent){
        String action = intent.getAction();
        String type = intent.getType();


        String s = "action=" + action + ",type = " + type;
        Log.d(TAG, s);
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();

        if(!action.equals(ACTION_MMS_RECEIVED))
            return;

        MainActivity main = MainActivity.getInstance();
        if(main != null){
            //main.addLog("action=" + action + ",type=" + type);
        }

        Bundle bundle = intent.getExtras();
        if(bundle == null)
            return;


        byte[] buffer = bundle.getByteArray("data");
        String incomingNumber = new String(buffer);

        MMSInfo info = MMSInfo.fromByteArray(buffer);

        Log.d(TAG, info.messageType + "," + info.transactionId + ",version=" + info.version+ ",from=" + info.from + ",subject=" + info.subject);

        Log.d(TAG, "data.length=" + buffer.length + ",value='" + bytesToHex(buffer) + "'");
        Log.d(TAG, incomingNumber);

        int indx = incomingNumber.indexOf("/TYPE");
        if(indx>0 && (indx-15)>0){
            int newIndx = indx - 15;
            incomingNumber = incomingNumber.substring(newIndx, indx);
            indx = incomingNumber.indexOf("+");
            if(indx>0){
                incomingNumber = incomingNumber.substring(indx);
            }
        }

        int pduType = bundle.getInt("pduType");
        byte[] buffer2 = bundle.getByteArray("header");
        String header = new String(buffer2);

        Log.d(TAG, "header.length=" + buffer2.length + ",value='" + bytesToHex(buffer2) + "'");

        s = "number=" + incomingNumber + "\ntype=" + String.valueOf(pduType) + "\nheader=" + header;
        Log.d(TAG, s);

        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }





}
