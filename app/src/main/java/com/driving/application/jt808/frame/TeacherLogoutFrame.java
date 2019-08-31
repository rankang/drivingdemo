package com.driving.application.jt808.frame;

import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Tools;

/**
 * 教练登出帧封装
 */
public class TeacherLogoutFrame extends JT808ExtFrame {
    private byte dataType;
    private byte[] teacherLoginNum;
    private String teacherIc;
    private int teacherNum;
    private byte[] reverse;
    private int schoolNum;
    private GpsPackage gps;

    public TeacherLogoutFrame(int key, int vendorId, String terminalPhoneNumber,
                              byte dataType, byte[] teacherLoginNum, String teacherIc,
                              int teacherNum, byte[] reverse,
                              int schoolNum, GpsPackage gps) {
        super(MSGID.TEACHER_LOGOUT_REQUEST, key, vendorId, terminalPhoneNumber);
        this.dataType = dataType;
        this.teacherLoginNum = teacherLoginNum;
        this.teacherIc = teacherIc;
        this.reverse = reverse;
        this.teacherNum = teacherNum;
        this.schoolNum = schoolNum;
        this.gps = gps;
    }

    @Override
    protected byte[] createMsgBody() {
        byte[] transBody = new byte[81];
        int index = 0;
        // 数据类型	BYTE	0：实时数据
        //1：补传数据
        transBody[index++] = dataType;
        //  教练登录编号	BYTE (6)
        for(byte b : teacherLoginNum) {
            transBody[index++] = b;
        }
        // 教练IC卡号	STRING(18)
        byte[] teacherIcByteArray = teacherIc.getBytes();
        for(byte b : teacherIcByteArray) {
            transBody[index++] = b;
        }
        // 教练编号	DWORD
        byte[] teacherNumByteArray = Tools.intTo4Bytes(teacherNum);
        for (byte b : teacherNumByteArray) {
            transBody[index++] = b;
        }
        // 保留	18
        for(byte b : reverse) {
            transBody[index++] = b;
        }
        // 教练驾校编号	DWORD
        byte[] schoolNumByteArray = Tools.intTo4Bytes(schoolNum);
        for(byte b : schoolNumByteArray) {
            transBody[index++] = b;
        }
        // GPS	GPS数据包
        byte[] gpsData = createGpsData(gps);
        for(byte b : gpsData) {
            transBody[index++] = b;
        }
        return transBody;
    }
}
