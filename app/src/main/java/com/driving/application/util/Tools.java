package com.driving.application.util;

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
        byte[] dateArray = new byte[size-1];
        int index = 0;
        for(int i=2; i<date.length(); i+=2) {
            String each = date.substring(i, i+2);
            dateArray[index++] = (byte) Integer.parseInt(each);
        }
        return bcdExchange(dateArray);
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

    public static int byte2Int(byte b2, byte b3, byte b0, byte b1) {
        byte[] b = {b2, b3, b0, b1};
        return byte2Int(b);
    }

    /**
     * 	1、A: 为终端注册成功获取的鉴权码
     * 	2、B: 设备商key1码，运管分配(书信密函)
     * 	3、C: 设备商key2码，运管分配(书信密函)
     */
    private final int M1 = A;
    private final int IA1 = B;
    private final int IC1 = C;
    public byte[] encry(int key, byte[] buffer, int size) {
        int idx = 0;
        if(0 == key) {
            key = 1;
        }
        int mkey = M1;
        if(0 == mkey) {
            mkey = 1;
        }
        while (idx<size) {
            key = IA1 * (key % mkey) +IC1;
            buffer[idx++]^= (char)((key >> 20) &0xff);
        }
        return buffer;
    }

}
