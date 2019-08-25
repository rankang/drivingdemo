package com.driving.application.jt808.frame;

import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;

public class StudentLoginFrame extends JT808ExtFrame {
    public StudentLoginFrame(int key, int vendorId, String terminalPhoneNumber) {
        super(MSGID.STUDENT_LOGIN, key, vendorId, terminalPhoneNumber);
    }

    @Override
    protected byte[] createMsgBody() {
        return new byte[0];
    }
}
