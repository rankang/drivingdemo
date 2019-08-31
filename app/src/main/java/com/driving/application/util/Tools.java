package com.driving.application.util;

import android.util.Log;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class Tools {

    /**字节数组转成16进制字符串*/
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**一个字节转16进制*/
    public static String byteToHexString(byte buf) {
        byte[] src = new byte[]{buf};
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString() + " ";
    }


    /**获取BCD码
     * @return 19 08 14 16 11 05 -> 20190814161105
     * */
    public static byte[] getBCDByteArray(String date) {
        // 年取2位 size - 1
        int size = date.length()/2;
        byte[] dateArray = new byte[size];
        int index = 0;
        for(int i=0; i<date.length(); i+=2) {
            String each = date.substring(i, i+2);
            dateArray[index++] = (byte) Integer.parseInt(each);
        }
        return bcdExchange(dateArray);
    }

    /**
     *  把电话号码转成BCD码
     */
    public static byte[] getPhoneNumberBCD(String phoneNumber) {
        // 用6个字节保存
        byte[] phoneNumberBcd = new byte[6];
        int reverseCount = 12-phoneNumber.length();
        StringBuilder reverse = new StringBuilder();
        for(int i = 0; i < reverseCount; i++) {
            reverse.append("0");
        }
        String fixPhoneNumber = reverse + phoneNumber;
        int index = 0;
        for(int i=0; i<fixPhoneNumber.length(); i+=2) {
            String each = fixPhoneNumber.substring(i, i+2);
            phoneNumberBcd[index++] = (byte) Integer.parseInt(each);
        }
        return bcdExchange(phoneNumberBcd);
    }

    private static byte[] bcdExchange(byte[] data) {
        byte[] hexData = new byte[data.length];
        for(int i=0; i<data.length; i++) {
            String numStr = String.valueOf(data[i]);
            Integer bcd = Integer.parseInt(numStr, 16);
            hexData[i] = (byte) bcd.intValue();
        }
        return hexData;
    }

    /**int 转2个字节*/
    public static byte[] intTo2Bytes(int n) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            b[i] = (byte) (n >> (8 - i * 8));
        }
        return b;
    }

    public static byte[] intTo4Bytes(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }

    /**高位在前， 4个字节转int*/
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

    /**
     * 两个字节转int
     * @param b
     * @return
     */
    public static int twoBytes2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (1 - i));
        }
        return intValue;
    }

    public static int byte2Int(byte b2, byte b3, byte b0, byte b1) {
        byte[] b = {b2, b3, b0, b1};
        return byte2Int(b);
    }




    private static String integer2BitS(int num) {
        int a; //获取余数
        int ch=0; //存储二进制
        int i=0; //存储除了几次
        while(num!=0){
            a=num%2;
            num=num/2;
            ch+=a*(Math.pow(10, i));
            i++;
        }
        return String.valueOf(ch);
    }

    /**
     * 	1、A: 为终端注册成功获取的鉴权码
     * 	2、B: 设备商key1码，运管分配(书信密函)
     * 	3、C: 设备商key2码，运管分配(书信密函)
     * 	加密内容： 透传消息内容
     * 	加密解密方法
     */
    private static final int IA1 = 9100000;
    private static final int IC1 = 9200000;
    public static byte[] encrypt(int key, byte[] buffer, int size) {
        int M1 = Integer.parseInt(Utils.validateCode);
        byte[] encrypt = new byte[size];
        int idx = 0;
        if(0 == key) {
            key = 1;
        }
        int mkey = M1;
        if(0 == mkey) {
            mkey = 1;
        }
        while (idx < size) {
            key = IA1 * (key % mkey) +IC1;
            encrypt[idx] = (byte) (buffer[idx] ^  ((key >> 20) & 0xff));
            idx++;
        }
        return encrypt;
    }

    public static byte checkSum(byte[] data) {
        return checkSum(data, 0, data.length);
    }

    public static byte checkSum(byte[] data, int start, int length) {
        // checkSum 计算
        byte checkSum = data[start];
        for(int i=(start+1); i< start+length; i++) {
            checkSum ^=  data[i];
        }
        return checkSum;
    }

}
