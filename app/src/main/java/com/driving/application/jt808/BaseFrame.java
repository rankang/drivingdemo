package com.driving.application.jt808;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

public class BaseFrame {
    private static int flowNum = 1;
    public static final int FLAG = 0x7E;
    /**
     *  获取flow num [1-65535]
     * @return flow num
     */
    public static int getFlowNum() {
        if(flowNum > 65535) {
            flowNum = 1;
        }
        return flowNum++;
    }

    public byte[] transformer(byte[] frameData) {
        int count = 0;
        for(int i=1; i < frameData.length-1; i++) {
            if(frameData[i] == 0x7e || frameData[i] == 0x7d){
                count++;
            }
        }
        Logger.i(Tools.bytesToHexString(frameData));
        byte[] data = new byte[frameData.length + count];
        int index = 0;
        data[index++] = FLAG;
        for(int i=1; i < (frameData.length-1); i++) {
            if(frameData[i] == 0x7e) {
                data[index++] = 0x7d;
                data[index++] = 0x02;
            } else if(frameData[i] == 0x7d) {
                data[index++] = 0x7d;
                data[index++] = 0x01;
            } else {
                data[index++] = frameData[i];
            }
        }

        data[index] = FLAG;
        return data;
    }
}
