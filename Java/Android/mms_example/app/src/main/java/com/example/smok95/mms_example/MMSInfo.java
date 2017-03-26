package com.example.smok95.mms_example;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    private String m_transactionId;
    private String m_from;
    private String m_subject;
    private int m_messageType;
    private String m_version;

    private ByteArrayInputStream m_bis = null;

    private boolean initFromByteArray(byte[] raw)
    {
        final int MESSAGE_TYPE = 0x83;
        final int TRANSACTION_ID = 0x98;
        final int MMS_VERSION = 0x8d;
        final int FROM = 0x89;
        final int SUBJECT = 0x96;

         /*
        관련 정보
        http://m.blog.naver.com/hkbemil/130181128951
        http://intercontineo.com/article/9848315724/
        http://www.phonesdevelopers.info/1700063/
        https://android.googlesource.com/platform/frameworks/opt/mms/+/4bfcd8501f09763c10255442c2b48fad0c796baa/src/java/com/google/android/mms/pdu/PduHeaders.java
        https://android.googlesource.com/platform/frameworks/opt/mms/+/a81f07778a3400a7839564f9fc027b1548329004/src/java/com/google/android/mms/pdu/PduParser.java

        * to decode the subject field of MMS Notification...
        https://support.nowsms.com/discus/messages/12/3746.html
        * mms-decoder
        https://github.com/heyman/mms-decoder/blob/master/mmsdecoder.php

        현재까지 확인한 데이터 형식은 field값(1byte) 값 이며 00이 오면 값의 종료를 뜻하며 아닌 경우도 있음.

        8C : X-Mms-Message-Type
            82 : MESSAGE_TYPE_NOTIFICATION_IND
        98 : TRANSACTION_ID

        수신 data예
        data.length=140,
        value='8C829830396F334A30304B563031008D9289178030313038393939303532302F545950453D504C4D4E00961F20EA74686973206973207375626A65637420ECA09CEBAAA9EC9DB4EC95BC40400086818A808F818E0202CF8805810324EA0083687474703A2F2F6F6D6D732E6E6174652E636F6D3A393038332F6D6D736431322F30396F334A30304B56303100'
        */

        if(raw == null || raw.length==0)
            return false;

        m_bis = new ByteArrayInputStream(raw);
        int fieldType = 0;

        while(m_bis.available() > 0)
        {
            fieldType = m_bis.read();
            if(fieldType == -1)
                return false;

            switch (fieldType)
            {
                case MESSAGE_TYPE:{
                    m_messageType = m_bis.read();
                }break;
                case TRANSACTION_ID:{
                    m_transactionId = parseTextString();
                }break;

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
                pos++;

                // 2nd byte는 Address-Presentation-Token
                pos++;

                int idx = findIndex(data, pos, (byte)0);
                if(idx == -1)
                    return info;

                info.from = new String(Arrays.copyOfRange(data, pos, idx));
                pos = idx;

                // From에서 "/TYPE=PLMN" 제거
                idx = info.from.indexOf("/TYPE=PLMN");
                if(idx != -1)
                    info.from = info.from.substring(0, idx);
            }
            else if(fieldType == SUBJECT)
            {
                if(pos >= data.length)
                    break;
                // see also : https://github.com/heyman/mms-decoder/blob/master/mmsdecoder.php
                info.subject  = parseEncodedStringValue(data, pos);
                pos = ;
            }
        }
        return info;
    }

    /**
     * Parse Text-string
     * text-string = [Quote <Octet 127>] text [End-string <Octet 00>]
     */
    private String parseTextString(){
        if(m_bis == null || m_bis.available()==0)
            return "";

        int val = m_bis.read();
        if(val == -1)
            return "";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Remove quote(127)
        if(val != 0x7F)
            bos.write(val);

        while(true)
        {
            val = m_bis.read();
            if(val == 0x00)
                break;
            bos.write(val);
        }
        return new String(bos.toByteArray());
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
        return new MMSInfo(data);
    }

    private static String parseEncodedStringValue(byte[] v, int pos)
    {
        // 문자열의 끝을 검색한다 (string zero)
        int idx = findIndex(v, pos, (byte)0);
        if(idx == -1)
            return "";


        // character set
        final int CS_UTF8 = 0xEA;
        final int CS_ASCII = 0x83;
        final int CS_ISO_8859_1 = 0x84;
        final int CS_ISO_8859_2 = 0x85;
        final int CS_ISO_8859_3 = 0x86;
        final int CS_ISO_8859_4 = 0x87;

        int len = 0; // 문자열 길이

        if(v[pos] <= 31)
        {
            if(v[pos] < 31)
                len = v[pos++];
            else if(v[pos] == 31){
                // got the quote, length is an uint
                pos++;

                while((v[pos] & 0x80) > 0){
                    // Shift the current value 7 steps
                    len = len << 7;
                    // Remove the first bit of the byte and add it to the current value
                    len |= v[pos++] & 0x7f;
                }

                // Shift the current value 7 steps
                len = len << 7;
                // Remove the first bit of the byte and add it to the current value
                len |= v[pos++] & 0x7f;
            }

            // Remove quote
            if(v[pos] == 0x7f)
                pos++;

            int charSet = (int)v[pos++];

            Log.d(TAG, "encoded string len=" + (idx-pos));
            return new String(v, pos, idx-pos);
        }
        else {
            // Remove quote
            if(v[pos] == 0x7f)
                pos++;
            return new String(v, pos, idx-pos);
        }
    }

}
