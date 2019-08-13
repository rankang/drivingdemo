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


    /**获取BCD码*/
    public static byte[] getBCDByteArray(String date) {
        int size = date.length()/2;
        byte[] dateArray = new byte[size];
        int index = 0;
        for(int i=0; i<date.length(); i+=2) {
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

}
