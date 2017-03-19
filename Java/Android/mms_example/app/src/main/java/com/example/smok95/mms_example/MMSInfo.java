package com.example.smok95.mms_example;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by smok95 on 19/03/2017.
 */

public class MMSInfo {
    private static final String TAG = "MMSInfo";

    public static final byte MMS_VERSION_1_3 = (byte)0x93;
    public static final byte MMS_VERSION_1_2 = (byte)0x92;
    public static final byte MMS_VERSION_1_1 = (byte)0x91;
    public static final byte MMS_VERSION_1_0 = (byte)0x90;


    public String transactionId;
    public String from;
    public String subject;
    public int messageType;
    public String version;

    {
        messageType = 0;

    }


    public static int findIndex(byte[] src, int startIndex, byte findValue)
    {
        if(src.length <= startIndex)
            return -1;

        for(int i=startIndex; i<src.length; i++)
        {
            if(src[i] == findValue)
                return i;
        }
        return -1;
    }

    public static MMSInfo fromHexString(String str)
    {
        byte[] data = new byte[str.length()/2];
        for(int i=0; i<str.length(); i+= 2){
            data[i/2] = (byte)((Character.digit(str.charAt(i),16) << 4) +
                    Character.digit(str.charAt(i+1), 16));
        }

        return fromByteArray(data);
    }

    public static MMSInfo fromByteArray(byte[] data)
    {
        /*
        관련 정보
        http://m.blog.naver.com/hkbemil/130181128951
        http://intercontineo.com/article/9848315724/
        http://www.phonesdevelopers.info/1700063/
        https://android.googlesource.com/platform/frameworks/opt/mms/+/4bfcd8501f09763c10255442c2b48fad0c796baa/src/java/com/google/android/mms/pdu/PduHeaders.java
        https://android.googlesource.com/platform/frameworks/opt/mms/+/a81f07778a3400a7839564f9fc027b1548329004/src/java/com/google/android/mms/pdu/PduParser.java

        현재까지 확인한 데이터 형식은 field값(1byte) 값 이며 00이 오면 값의 종료를 뜻하며 아닌 경우도 있음.

        8C : X-Mms-Message-Type
            82 : MESSAGE_TYPE_NOTIFICATION_IND
        98 : TRANSACTION_ID

        수신 data예
        data.length=140,
        value='8C829830396F334A30304B563031008D9289178030313038393939303532302F545950453D504C4D4E00961F20EA74686973206973207375626A65637420ECA09CEBAAA9EC9DB4EC95BC40400086818A808F818E0202CF8805810324EA0083687474703A2F2F6F6D6D732E6E6174652E636F6D3A393038332F6D6D736431322F30396F334A30304B56303100'
        */
        final byte MESSAGE_TYPE = (byte)0x83;
        final byte TRANSACTION_ID = (byte)0x98;
        final byte MMS_VERSION = (byte)0x8d;
        final byte FROM = (byte)0x89;
        final byte SUBJECT = (byte)0x96;

        MMSInfo info = new MMSInfo();


        int pos = 0;

        byte fieldType = 0;
        while(pos < data.length)
        {
            fieldType = data[pos];

            Log.d("HELLO", String.format("fieldType[%03d]=%02x, %d", pos, fieldType, fieldType));

            pos++;
            if(fieldType == MESSAGE_TYPE)
            {
                if(pos >= data.length)
                    break;

                info.messageType = data[pos];
            }
            else if(fieldType == TRANSACTION_ID) {
                int idx = findIndex(data, pos, (byte)0);
                if(idx == -1)
                    return info;

                info.transactionId= new String(Arrays.copyOfRange(data, pos, idx));
                pos = idx;
            }
            else if(fieldType == MMS_VERSION)
            {
                if(pos >= data.length)
                    break;

                switch (data[pos])
                {
                    case MMS_VERSION_1_3:   info.version= "1.3"; break;
                    case MMS_VERSION_1_2:   info.version = "1.2"; break;
                    case MMS_VERSION_1_1:   info.version = "1.1"; break;
                    case MMS_VERSION_1_0:   info.version = "1.0"; break;
                    default:                info.version = "1.2"; break;

                }
            }
            else if(fieldType == FROM)
            {
                if(pos >= data.length)
                    break;

                // 1st byte는 Length of Sender ID(as per wap-230 spec : page 83, this number must be less than 1F)
                Log.d(TAG, String.format("From Length=%d", data[pos]));
                pos++;

                // 2nd byte는 Address-Presentation-Token
                Log.d(TAG, String.format("AddressPresentationToken=%2x", data[pos]));
                pos++;

                int idx = findIndex(data, pos, (byte)0);
                if(idx == -1)
                    return info;

                info.from = new String(Arrays.copyOfRange(data, pos, idx));
                pos = idx;

                // From에서 "/TYPE=PLMN" 제거
                idx = info.from.indexOf("/TYPE=PLMN");
                if(idx != -1)
                    info.from = info.from.substring(0, idx-1);
            }
            else if(fieldType == SUBJECT)
            {
                if(pos >= data.length)
                    break;
                int idx = findIndex(data, pos, (byte)0);
                if(idx == -1)
                    return info;
                info.subject = new String(Arrays.copyOfRange(data, pos, idx));
                pos = idx;
            }
        }


        return info;
    }

}
