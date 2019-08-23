package com.driving.application.jt808.frame;

import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Utils;

import java.nio.charset.Charset;

public class ValidateFrame extends JT808StFrame {

    private String validateCode;
    public ValidateFrame(String phoneNumber, String validateCode) {
        super(MSGID.VALIDATE_REQ, phoneNumber);
        this.validateCode = validateCode;
    }

    @Override
    protected byte[] createMsgBody() {
        byte [] body = validateCode.getBytes(Charset.forName("gbk"));
        return body;
    }
}
