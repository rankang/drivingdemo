package com.driving.application.jt808.frame;

import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Tools;

import java.nio.charset.Charset;

/**
 * 学员登录帧
 */
public class StudentLoginFrame extends JT808ExtFrame {
    private byte dataType;
    private byte[] studentLoginNum;
    private String studentIcCard;
    private int studentNum;
    private byte[] reverse;
    private byte grade;
    private int cTeachNum;
    private int schoolNum;
    private int picId;
    private GpsPackage gpsPackage;

    public StudentLoginFrame(int key, byte dataType, int vendorId, String terminalPhoneNumber,
                             byte[] studentLoginNum, String studentIcCard, int studentNum, byte[] reverse,
                             byte grade, int cTeachNum, int schoolNum, int picId, GpsPackage gpsPackage) {
        super(MSGID.STUDENT_LOGIN_REQ, key, vendorId, terminalPhoneNumber);
        this.dataType =dataType;
        this.studentLoginNum = studentLoginNum;
        this.studentIcCard = studentIcCard;
        this.studentNum = studentNum;
        this.reverse = reverse;
        this.grade = grade;
        this.cTeachNum = cTeachNum;
        this.schoolNum = schoolNum;
        this.picId = picId;
        this.gpsPackage = gpsPackage;
    }

    @Override
    protected byte[] createMsgBody() {
        byte[] transBody = new byte[92];
        int index = 0;
        // 透传消息类型
        //0：实时数据
        //1：补传数据
        transBody[index++] = dataType;
        // 学员登录编号 8byte
        for(byte b : studentLoginNum) {
            transBody[index++] = b;
        }
        // 学员IC卡号 18byte
        byte[] studentIcCardArray = studentIcCard.getBytes(Charset.forName("gbk"));
        for(byte b : studentIcCardArray) {
            transBody[index++] = b;
        }
        // 学员编号 4byte
        byte[] studentNumByteArray = Tools.intTo4Bytes(studentNum);
        for(byte b : studentNumByteArray) {
            transBody[index++] = b;
        }
        // 保留 18byte
        for(byte b: reverse) {
            transBody[index++] = b;
        }
        // 学习科目 1byte
        transBody[index++] = grade;
        // 当前教练编号 4byte
        byte[] teacherNumByteArray = Tools.intTo4Bytes(cTeachNum);
        for(byte b : teacherNumByteArray) {
            transBody[index++] = b;
        }
        // 学员驾校编号 4byte
        byte[] schoolByteArray = Tools.intTo4Bytes(schoolNum);
        for(byte b : schoolByteArray) {
            transBody[index++] = b;
        }
        // 多媒体ID 4byte
        byte[] picIdByteArray = Tools.intTo4Bytes(picId);
        for(byte b : picIdByteArray) {
            transBody[index++] = b;
        }
        // GPS数据包 30个字节
        byte[] gpsData = createGpsData(gpsPackage);
        for(byte b : gpsData) {
            transBody[index++] = b;
        }
        return transBody;
    }
}
