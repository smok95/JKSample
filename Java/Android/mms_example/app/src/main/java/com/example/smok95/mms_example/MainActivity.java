package com.example.smok95.mms_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static MainActivity gInstance = null;
    private EditText mEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEditText = (EditText)findViewById(R.id.mEdit);

        gInstance = this;
        setContentView(R.layout.activity_main);

        String strSample = "8C829830396F334A30304B563031008D9289178030313038393939303532302F545950453D504C4D4E00961F20EA74686973206973207375626A65637420ECA09CEBAAA9EC9DB4EC95BC40400086818A808F818E0202CF8805810324EA0083687474703A2F2F6F6D6D732E6E6174652E636F6D3A393038332F6D6D736431322F30396F334A30304B56303100";

        MMSInfo info = MMSInfo.fromHexString(strSample);

        Log.d(TAG, info.getMessageType() + "," + info.getTransactionId()+ ",version=" + info.getVersion()+ ",from=" + info.getFrom()+ ",subject=" + info.getSubject());
    }


    public static MainActivity getInstance(){
        return gInstance;
    }


    public void addLog(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEditText.append(msg+"\n");
            }
        });
    }
}
