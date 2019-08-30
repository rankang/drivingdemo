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
        super(MSGID.TEACHER_LOGIN_REQ, key, vendorId, terminalPhoneNumber);
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

        // 教练登录编号6给字节 BCD码+流水号
        // 2019年08 月22 日22时
        byte[] bcdDateTime = Tools.getBCDByteArray(dateHour);
        byte[] teacherLoginByteArray = new byte[6];
        int k = 0;
        for(byte b : bcdDateTime) {
            teacherLoginByteArray[k++] = b;
        }
        int teacherLoginFlowNum = PrefsUtil.getTeachLoginFlowNum();
        byte[] flowNumBytes = Tools.intTo2Bytes(teacherLoginFlowNum); // 教练登录流水号1-65535
        for(byte b : flowNumBytes) {
            teacherLoginByteArray[k++] = b;
        }
        Utils.teacherLoginNumByteArray = teacherLoginByteArray;
        int index = 0;
        //0：实时数据
        //1：补传数据
        // 由于只考虑在线情况所以此处赋值为0
        transBody[index++] = dataType;
        // 教练登录编号
        for(byte b : teacherLoginByteArray) {
            transBody[index++]  = b;
        }

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


}
