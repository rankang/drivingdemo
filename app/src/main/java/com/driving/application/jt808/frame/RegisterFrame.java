package com.driving.application.jt808.frame;

import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Tools;

import java.nio.charset.Charset;

public class RegisterFrame extends JT808StFrame {

    // 530101 000000 取前两位和后4位-> 00 35 00 00
    // 省份ID
    protected byte[] provinceId = {0x00, 0x35};
    // 市域ID
    protected byte[] cityId = {0x00, 0x00};
    // 厂商ID 自定义5个字节
    protected byte[] vendorIdBytes = {0x53, 0x31, 0x30, 0x30,  0x30};
    // 终端型号， 自定义 8个字节
    protected byte[] terminalModelBytes = {0x53, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
    // 终端ID 自定义7个字节
    protected byte[] terminalIdBytes = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};
    // 车牌号码 默认0x02
    protected byte carBoardColor = 0x02;

    // 车牌号
    private String carNumber;

    public RegisterFrame(String phoneNumber, String carNumber) {
        super(MSGID.REGISTER_REQ, phoneNumber);
        this.carNumber = carNumber;
    }


    @Override
    protected byte[] createMsgBody() {
        byte[] carNumberBytes = carNumber.getBytes(Charset.forName("gbk"));
        // 初始化body字节数组
        byte[] body = new byte[25+carNumberBytes.length];
        int index = 0;
        // 530101 000000 取前两位和后4位-> 00 35 00 00
        // 省域ID
        //byte[] provinceBytes = {0x00, 0x35};
        body[index++] = provinceId[0];
        body[index++] = provinceId[1];
        //  市县域ID
        body[index++] = cityId[0];
        body[index++] = cityId[1];
        // 制造商ID 自定义
        //byte[] vendorIdBytes = {0x53, 0x31, 0x30, 0x30,  0x30}; // 制造商ID =5
        for(byte b : vendorIdBytes) {
            body[index++] = b;
        }
        // 终端型号， 自定义 8个字节
        //byte[] terminalModelBytes = {0x53, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
        for(byte b : terminalModelBytes) {
            body[index++] = b;
        }
        // 终端ID 自定义7个字节
        //byte[] terminalIdBytes = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};
        for(byte b : terminalIdBytes) {
            body[index++] = b;
        }
        // 车牌颜色 默认
        body[index++] = carBoardColor; // 车牌颜色
        // 车牌 需要和手机号对应
        for(byte b : carNumberBytes) {
            body[index++] = b;
        }
        return body;
    }
}
