package com.driving.application.jt808;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

public abstract class BaseFrame {
    private static int internalIndex = 1;
    /**帧标识符*/
    protected static final int FLAG = 0x7E;
    /**
     *  获取flow num [1-65535]
     * @return flow num
     */
    protected  int getFlowNum() {
        if(internalIndex > 65535) {
            internalIndex = 1;
        }
        return internalIndex++;
    }

    protected byte[] transformer(byte[] frameData) {
        int count = 0;
        // 处理 header + body + check sum
        byte[] tempFrameData = new byte[frameData.length - 2];

        for(int i=0; i <= frameData.length-1; i++) {
            if(i != 0 && i != frameData.length-1) {
                if(frameData[i] == 0x7e || frameData[i] == 0x7d){
                    count++;
                }
                tempFrameData[i-1] = frameData[i];
            }
        }

        Logger.i("-----------transformer--tempFrameData--------"+Tools.bytesToHexString(tempFrameData));
        if(count <= 0) return frameData;

        byte[] data = new byte[frameData.length + count];
        int index = 0;
        data[index++] = FLAG;
        //
        for(int i=0; i < tempFrameData.length; i++) {
            if(tempFrameData[i] == 0x7e) {
                data[index++] = 0x7d;
                data[index++] = 0x02;
            } else if(tempFrameData[i] == 0x7d) {
                data[index++] = 0x7d;
                data[index++] = 0x01;
            } else {
                data[index++] = tempFrameData[i];
            }
        }
        data[index] = FLAG;
        return data;
    }

    protected abstract byte[] createMsgHeader(int bodySize);
    protected abstract byte[] createMsgBody();
    public abstract byte[] getMessage();
}
