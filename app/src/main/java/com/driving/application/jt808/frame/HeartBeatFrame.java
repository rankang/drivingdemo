package com.driving.application.jt808.frame;

import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.MSGID;

/**
 * 心跳帧，消息体为空
 */
public class HeartBeatFrame extends JT808StFrame {

    public HeartBeatFrame(String phoneNumber) {
        super(MSGID.HEART_BEAT, phoneNumber);
    }

    @Override
    protected byte[] createMsgBody() {
        return new byte[0];
    }
}
