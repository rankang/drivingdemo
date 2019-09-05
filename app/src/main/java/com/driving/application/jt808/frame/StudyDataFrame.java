package com.driving.application.jt808.frame;

import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

/**
 * 学习数据通信帧
 * @author rankang
 */
public class StudyDataFrame extends JT808ExtFrame {
    private byte reportType;
    private byte[] studyNum;
    private String startRecordTime;
    private String endRecordTime;
    private int studyTime;
    private int driveKm;
    private int studentNum;
    private int teacherNum;
    private byte grade;
    private int status;
    private String studentIC;
    private String teacherIC;
    private GpsPackage gps;

    /**
     *
     * @param key 加密key
     * @param vendorId 厂商id
     * @param terminalPhoneNumber  终端号码
     * @param reportType 上报类型
     * @param studyNum 学时记录编号
     * @param startRecordTime 记录开始编号
     * @param endRecordTime 记录结束编号
     * @param studyTime 学习学时
     * @param driveKm 学习里程
     * @param studentNum 学员编号
     * @param teacherNum 教练编号
     * @param grade 学习科目
     * @param status 学时状态
     * @param studentIIC 学员IC卡号
     * @param teacherIC 教练IC卡号
     * @param gps gps info
     */
    public StudyDataFrame(int key, int vendorId, String terminalPhoneNumber,
                          byte reportType, byte[] studyNum, String startRecordTime, String endRecordTime,
                          int studyTime, int driveKm, int studentNum, int teacherNum, byte grade, int status,
                          String studentIIC, String teacherIC, GpsPackage gps) {
        super(MSGID.UPLOAD_STUDY_DATA, key, vendorId, terminalPhoneNumber);
        this.reportType = reportType;
        this.studyNum = studyNum;
        this.startRecordTime = startRecordTime;
        this.endRecordTime = endRecordTime;
        this.studyTime = studyTime;
        this.driveKm = driveKm;
        this.studentNum = studentNum;
        this.teacherNum = teacherNum;
        this.grade = grade;
        this.status = status;
        this.studentIC = studentIIC;
        this.teacherIC = teacherIC;
        this.gps = gps;
    }

    @Override
    protected byte[] createMsgBody() {
        byte[] transBody = new byte[106];
        int index = 0;
        // 上报类型
        transBody[index++] = reportType;
        // 学时记录编号	BYTE(10)
        for(byte b : studyNum) {
            transBody[index++] = b;
        }
        // 记录开始时间	BCD(6)
        byte[] startRecordTimeByteArray = Tools.getBCDByteArray(startRecordTime);
        for(byte b : startRecordTimeByteArray) {
            transBody[index++] = b;
        }
        //记录结束时间	BCD(6)
        byte[] endRecordTimeByteArray = Tools.getBCDByteArray(endRecordTime);
        for(byte b : endRecordTimeByteArray) {
            transBody[index++] = b;
        }
        // 学习学时	WROD
        byte[] studyTimeByteArray = Tools.intTo2Bytes(studyTime);
        for(byte b : studyTimeByteArray) {
            transBody[index++] = b;
        }
        //行驶里程	DWORD
        byte[] driveKmByteArray = Tools.intTo4Bytes(driveKm);
        for(byte b : driveKmByteArray) {
            transBody[index++] = b;
        }
        // 学员编号	DWORD
        byte[] studentNumByteArray = Tools.intTo4Bytes(studentNum);
        for(byte b : studentNumByteArray) {
            transBody[index++] = b;
        }
        // 教练编号	DWORD
        byte[] teacherNumByteArray = Tools.intTo4Bytes(teacherNum);
        for(byte b : teacherNumByteArray) {
            transBody[index++] = b;
        }
        // 学习科目	BYTE
        transBody[index++] = grade;
        // 学时状态	WORD
        byte[] studyStatusByteArray = Tools.intTo2Bytes(status);
        for(byte b : studyStatusByteArray) {
            transBody[index++] = b;
        }
        // 学员IC卡号	STRING(18)
        byte[] studentIcByteArray = studentIC.getBytes();
        for(byte b : studentIcByteArray) {
            transBody[index++] = b;
        }
        // 教练IC卡号	STRING(18)
        byte[] teacherIcByteArray = teacherIC.getBytes();
        for(byte b : teacherIcByteArray) {
            transBody[index++] = b;
        }
        // GPS信息	GPS数据包
        byte[] gpsData = createGpsData(gps);
        for(byte b : gpsData) {
            transBody[index++] = b;
        }
        Logger.i("---------------------------"+Tools.bytesToHexString(transBody));
        return transBody;
    }
}
