package com.driving.application.jt808.frame;

import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Tools;

import java.nio.charset.Charset;

/**
 * 学员登出帧封装
 */
public class StudentLogoutFrame extends JT808ExtFrame {
    private byte dataType;
    private byte[] stuLoginNum;
    private String stuIc;
    private int stuNum;
    private byte[] reverse;
    private String startTime;
    private String endTime;
    private byte studyGrade;
    private int teacherNum;
    private int schoolNum;
    private byte loginOutCode;
    private int reverse2;
    private int curFinishKm;
    private int curFinishStudyHour;
    private int maxSpeed;
    private int mediaId;
    private GpsPackage gps;
    public StudentLogoutFrame(int key, int vendorId, String terminalPhoneNumber, byte dataType, byte[] stuLoginNum,
                              String stuIc, int stuNum, byte[] reverse, String startTime, String endTime, byte studyGrade,
                              int teacherNum, int schoolNum, byte loginOutCode, int reverse2, int curFinishKm,
                              int curFinishStudyHour, int maxSpeed, int mediaId, GpsPackage gps) {
        super(MSGID.STUDENT_LOGOUT_REQUEST, key, vendorId, terminalPhoneNumber);
        this.dataType = dataType;
        this.stuLoginNum =stuLoginNum;
        this.stuIc = stuIc;
        this.stuNum = stuNum;
        this.reverse = reverse;
        this.startTime = startTime;
        this.endTime = endTime;
        this.studyGrade =studyGrade;
        this.teacherNum = teacherNum;
        this.schoolNum = schoolNum;
        this.loginOutCode = loginOutCode;
        this.reverse2 = reverse2;
        this.curFinishKm = curFinishKm;
        this.curFinishStudyHour = curFinishStudyHour;
        this.maxSpeed = maxSpeed;
        this.mediaId = mediaId;
        this.gps = gps;
    }

    @Override
    protected byte[] createMsgBody() {
        byte[] transBody = new byte[115];
        int index = 0;
        transBody[index++] = dataType;
        // 学员登录编号 BYTE(8)
        for (byte b : stuLoginNum) {
            transBody[index++] = b;
        }
        // 学员IC卡号	STRING(18)
        byte[] stuIcByteArray = new byte[18];
        byte[] temp = stuIc.getBytes(Charset.forName("gbk"));
        System.arraycopy(temp, 0, stuIcByteArray, 0, temp.length);
        for(byte b : stuIcByteArray) {
            transBody[index++] = b;
        }
        // 学员编号	DWORD
        byte[] stuNumByteArray = Tools.intTo4Bytes(stuNum);
        for (byte b : stuNumByteArray) {
            transBody[index++] = b;
        }
        // 保留	18
        for(byte b : reverse) {
            transBody[index++] = b;
        }
        // 开始时间	BCD(6)
        byte[] startTimeByteArray  = Tools.getBCDByteArray(startTime);
        for (byte b : startTimeByteArray) {
            transBody[index++] = b;
        }
        // 结束时间	BCD(6)
        byte[] endTimeByteArray  = Tools.getBCDByteArray(endTime);
        for (byte b : endTimeByteArray) {
            transBody[index++] = b;
        }
        // 学习科目	BYTE
        transBody[index++] = studyGrade;
        // 教练编号	DWORD
        byte[] teacherNumByteArray = Tools.intTo4Bytes(teacherNum);
        for (byte b : teacherNumByteArray) {
            transBody[index++] = b;
        }
        // 驾校编号	DWORD
        byte[] schoolNumByteArray = Tools.intTo4Bytes(schoolNum);
        for (byte b : schoolNumByteArray) {
            transBody[index++] = b;
        }
        // 登出代码	BYTE	0x00：正常退出 0x01：未验证指纹退出
        transBody[index++] = loginOutCode;
        // 保留	WORD
        byte[] reverse2ByteArray = Tools.intTo2Bytes(reverse2);
        for (byte b : reverse2ByteArray) {
            transBody[index++] = b;
        }
        // 本次完成里程	DWORD
        byte[] curFinishKmByteArray = Tools.intTo4Bytes(curFinishKm);
        for(byte b : curFinishKmByteArray) {
            transBody[index++] = b;
        }
        // 本次完成学时	WORD
        byte[] curHour = Tools.intTo2Bytes(curFinishStudyHour);
        for (byte b : curHour) {
            transBody[index++] = b;
        }
        // 最高时速	WORD
        byte[] maxSpeedByteArray = Tools.intTo2Bytes(maxSpeed);
        for(byte b : maxSpeedByteArray) {
            transBody[index++] = b;
        }
        // 多媒体ID	DWORD
        byte[] mediaIdByteArray = Tools.intTo4Bytes(mediaId);
        for (byte b : mediaIdByteArray) {
            transBody[index++] = b;
        }
        // GPS数据包
        byte[] gpsData = createGpsData(gps);
        for(byte b : gpsData) {
            transBody[index++] = b;
        }
        return transBody;
    }
}
