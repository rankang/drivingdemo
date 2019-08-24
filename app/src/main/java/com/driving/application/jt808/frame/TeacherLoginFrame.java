package com.driving.application.jt808.frame;

import com.driving.application.jt808.BaseFrame;
import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Logger;
import com.driving.application.util.PrefsUtil;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeacherLoginFrame extends JT808ExtFrame {

    // GPS 数据包
    private GpsPackage mGpsPackage;
    // 数据类型
    // 0：实时数据
    //1：补传数据
    private byte dataType=0x00;

    // yyyyMMddHH
    private String dateHour;

    // 教练IC卡
    private String teacherIC;

    // 教练编号
    private int teachNum;

    // reverse
    private byte[] reverse = new byte[18];

    // 教练驾校编号
    private int schoolNum;



    public TeacherLoginFrame(int key, int vendorId, String terminalPhoneNumber, byte dataType, String dateHour, String teacherIC,
                             int teachNum, byte[] reverse, int schoolNum) {
        super(MSGID.TEACHER_LOGIN, key, vendorId, terminalPhoneNumber);
        this.dataType = dataType;
        this.dateHour = dateHour;
        this.teacherIC = teacherIC;
        this.teachNum = teachNum;
        this.reverse = reverse;
        this.schoolNum = schoolNum;
    }

    public TeacherLoginFrame(int key, int vendorId, String terminalPhoneNumber, byte dataType, String dateHour, String teacherIC,
                             int teachNum, byte[] reverse, int schoolNum, GpsPackage gpsPackage) {
        this(key, vendorId, terminalPhoneNumber, dataType, dateHour, teacherIC, teachNum, reverse, schoolNum);
        this.mGpsPackage = gpsPackage;
    }

    /**
     * 把基础数据的gps数据组装
     * @return
     */
    @Override
    protected byte[] createMsgBody() {

        //// 模拟数据
        // 教练：63071952   姚志明    身份证530127198509281710
        //驾校编号：53010098
        byte[] transBody = new byte[51];
        int index = 0;
        //0：实时数据
        //1：补传数据
        // 由于只考虑在线情况所以此处赋值为0
        transBody[index++] = dataType;
        // 教练登录编号6给字节 BCD码+流水号
        // 2019年08 月22 日22时
        byte[] bcdDateTime = Tools.getBCDByteArray(dateHour);
        for(int i=0; i<bcdDateTime.length; i++) {
            transBody[index++] = bcdDateTime[i];
        }
        int teacherLoginFlowNum = PrefsUtil.getTeachLoginFlowNum();
        PrefsUtil.updateTeachLoginFlowNum();
        byte[] flowNumBytes = Tools.intTo2Bytes(teacherLoginFlowNum); // 教练登录流水号1-65535
        transBody[index++] = flowNumBytes[0];
        transBody[index++] = flowNumBytes[1];

        // 教练IC
        byte[] originIcData = teacherIC.getBytes(Charset.forName("gbk"));
        byte[] icBytes = new byte[18];
        for(int i=0; i<originIcData.length; i++) {
            icBytes[i] = originIcData[i];
        }
        for(byte b : icBytes) {
            transBody[index++] = b;
        }

        // 教练编号
        byte[] teachNumBytes = Tools.intTo4Bytes(teachNum);
        for (byte item : teachNumBytes) {
            transBody[index++] = item;
        }
        // 消息项reverse 18个字节
        for(byte b : reverse) {
            transBody[index++] = b;
        }
        // 教练驾校编号
        byte[] schoolBytes = Tools.intTo4Bytes(schoolNum);
        for(byte b : schoolBytes) {
            transBody[index++] = b;
        }

        if(mGpsPackage != null) {
            byte[] gpsPackageData = createGpsData(mGpsPackage);
            int gpsDataLength = gpsPackageData.length;
            byte[] transBodyWithGps = new byte[transBody.length+gpsDataLength];
            int i = 0;
            for(byte b : transBody) {
                transBodyWithGps[i++] = b;
            }
            for(byte b : gpsPackageData) {
                transBodyWithGps[i++] = b;
            }
            return transBodyWithGps;
        }
        return transBody;
    }

    private byte[] createGpsData(GpsPackage gp) {
        if(gp == null) return null;
        int index = 0;
        // 初始化数据包 30 byte
        byte[] gpsData = new byte[30];
        // 时间BCD 码
        String datetime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).format(new Date());
        Logger.i("----------------------------"+datetime);
        byte[] bcdDateTime = Tools.getBCDByteArray(gp.time);
        for(int i=0; i<bcdDateTime.length; i++) {
            gpsData[index++] = bcdDateTime[i];
        }
        // 经度
        byte[] lngBytes = Tools.intTo4Bytes(gp.lng);
        for(byte b : lngBytes) {
            gpsData[index++] = b;
        }
        // 纬度
        byte[] latBytes = Tools.intTo4Bytes(gp.lat);
        for(byte b : latBytes) {
            gpsData[index++] = b;
        }

        // 高度
        byte[] heightBytes = Tools.intTo2Bytes(gp.height);
        gpsData[index++] = heightBytes[0];
        gpsData[index++] = heightBytes[1];

        // 速度
        byte[] speedBytes = Tools.intTo2Bytes(gp.speed);
        gpsData[index++] = speedBytes[0];
        gpsData[index++] = speedBytes[1];

        // 记录设备速度
        byte[] recordDevSpeedBytes = Tools.intTo2Bytes(gp.recordDevSpeed);
        gpsData[index++] = recordDevSpeedBytes[0];
        gpsData[index++] = recordDevSpeedBytes[1];

        // 方向 0-359度,正北0,顺时针
        byte[] directionBytes = Tools.intTo2Bytes(gp.direction);
        gpsData[index++] = directionBytes[0];
        gpsData[index++] = directionBytes[1];
        // 状态1 JT808状态定义
        byte[] status1Bytes = Tools.intTo4Bytes(gp.status1);
        for(byte b : status1Bytes) {
            gpsData[index++] = b;
        }
        // 状态2
        byte[] status2Bytes = Tools.intTo4Bytes(gp.status2);
        for(byte b : status2Bytes) {
            gpsData[index++] = b;
        }
        Logger.i("gps="+Tools.bytesToHexString(gpsData));
        return gpsData;
    }
}
