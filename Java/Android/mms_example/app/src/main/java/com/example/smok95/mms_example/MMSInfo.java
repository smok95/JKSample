package com.example.smok95.mms_example;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.jar.Pack200;

/**
 * Created by smok95 on 19/03/2017.
 */

public class MMSInfo {
    private static final String TAG = "MMSInfo";

    private String m_transactionId;
    private String m_from;
    private String m_subject;
    private int m_messageType;
    private String m_version;


    public String getTransactionId(){
        return m_transactionId;
    }

    public String getFrom(){
        return m_from;
    }

    public String getSubject(){
        return m_subject;
    }

    public int getMessageType(){
        return m_messageType;
    }

    public String getVersion(){
        return m_version;
    }

    private byte[] m_raw = null;
    private int m_pos = 0;

    private int readByte(){
        if(m_raw==null || m_raw.length <= m_pos)
            return -1;
        return m_raw[m_pos++] & 0xff;
    }
    private boolean initFromByteArray(byte[] raw)
    {
        final int MESSAGE_TYPE = 0x83;
        final int TRANSACTION_ID = 0x98;
        final int MMS_VERSION = 0x8d;
        final int FROM = 0x89;
        final int SUBJECT = 0x96;

        m_pos = 0;
        m_raw = null;
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

        m_raw = raw;

        int fieldType = 0;

        while(m_pos < m_raw.length)
        {
            fieldType = readByte();
            switch (fieldType)
            {
                case MESSAGE_TYPE:{
                    m_messageType = readByte();
                }break;
                case TRANSACTION_ID:{
                    m_transactionId = parseTextString();
                }break;
                case MMS_VERSION:{

                    final int MMS_VERSION_1_3 = 0x93;
                    final int MMS_VERSION_1_2 = 0x92;
                    final int MMS_VERSION_1_1 = 0x91;
                    final int MMS_VERSION_1_0 = 0x90;

                    switch (readByte()){
                        case MMS_VERSION_1_3:   m_version="1.3"; break;
                        case MMS_VERSION_1_2:   m_version="1.2"; break;
                        case MMS_VERSION_1_1:   m_version="1.1"; break;
                        case MMS_VERSION_1_0:   m_version="1.0"; break;
                        default:                m_version="1.2"; break;
                    }
                }break;
                case FROM:{
                    m_from = parseFromValue();

                    // From에서 "/TYPE=PLMN" 제거
                    int idx = m_from.indexOf("/TYPE=PLMN");
                    if(idx != -1)
                        m_from = m_from.substring(0, idx);
                }break;
                case SUBJECT:{
                    // see also : https://github.com/heyman/mms-decoder/blob/master/mmsdecoder.php
                    m_subject = parseEncodedStringValue();
                }break;
            }
        }

        return true;
    }

    /**
     * Parse From-value
     * From-value = Value=length (Address-present-token Encoded-string-value | Insert-address-token)
     *
     * Address-present-token = <Octet 128>
     * Insert-address-token = <Octet 129>
     * @return
     */
    private String parseFromValue(){
        final byte FROM_ADDRESS_PRESENT_TOKEN = (byte)0x80;
        final byte FROM_INSERT_ADDRESS_TOKEN = (byte)0x81;

        int len = parseValueLength();

        if(m_raw[m_pos] == FROM_ADDRESS_PRESENT_TOKEN){
            m_pos++;
            return parseEncodedStringValue();
        }
        else if(m_raw[m_pos] == FROM_INSERT_ADDRESS_TOKEN){
            m_pos++;
            return "";
        }
        else{
            // something is wrong since none of the tokens are present, try to skip this field
            m_pos += len;
            return "";
        }
    }

    /**
     * Parse Value-length
     * Value-length = Short-length<Octet 0-30> | Length-quote<Octet 31> Length<Uint>
     *
     * A list of content-types of a MMS message can be found here:
     * http://www.wapforum.org/wina/wsp-content-type.htm
     * @return the value length, or -1 if the got a problem.
     */
    private int parseValueLength(){
        if(m_raw[m_pos] < 31)
            return (int)m_raw[m_pos++]; // it's a short-length
        else if(m_raw[m_pos] == 31)
        {
            // got the quote, length is an Uint
            m_pos++;
            return parseUint();
        }
        else
            return -1;  // we got a problem.
    }

    private int parseUint(){
        int ret = 0;

        while((m_raw[m_pos] & (byte)0x80) != 0){
            // Shift the current value 7 steps
            ret = ret << 7;
            // Remove the first bit of the byte and add it to the current value
            ret |= m_raw[m_pos++] & (byte)0x7f;
        }

        // Shift the current value 7 steps
        ret = ret << 7;
        // Remove the first bit of the byte and add it to the current value
        ret |= m_raw[m_pos++] & (byte)0x7f;

        return ret;
    }

    /**
     * Parse Text-string
     * text-string = [Quote <Octet 127>] text [End-string <Octet 00>]
     */
    private String parseTextString(){
        if(m_raw==null || m_raw.length <= m_pos)
            return "";

        int val = readByte();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Remove quote(127)
        if(val != 0x7F)
            bos.write(val);

        while(true)
        {
            val = readByte();
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
        MMSInfo info = new MMSInfo();
        if(info.initFromByteArray(data))
            return info;
        else
            return null;
    }

    private String parseEncodedStringValue()
    {
        // character set
        final int CS_UTF8 = 0xEA;
        final int CS_ASCII = 0x83;
        final int CS_ISO_8859_1 = 0x84;
        final int CS_ISO_8859_2 = 0x85;
        final int CS_ISO_8859_3 = 0x86;
        final int CS_ISO_8859_4 = 0x87;

        if(m_raw[m_pos] <= 31){
            int len = parseValueLength();

            int charset = (int)m_raw[m_pos++];
        }
        return parseTextString();
    }

}
