package com.example.smok95.mms_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static MainActivity gInstance = null;
    private EditText mEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEditText = (EditText)findViewById(R.id.mEdit);

        gInstance = this;
        setContentView(R.layout.activity_main);

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
