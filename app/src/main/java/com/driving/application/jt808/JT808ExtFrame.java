package com.driving.application.jt808;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 基于JT/T808 扩展协议帧封装类
 * 协议采用大端模式(big-endian)
 * 标识位-消息头-消息体-校验码-标识位
 */
public abstract class JT808ExtFrame extends BaseFrame{

    private int msgId;
    private int key;
    private int vendorId;
    private String terminalPhoneNumber;
    protected int frameFlowNum = getFlowNum();
    public JT808ExtFrame(int msgId, int key, int vendorId, String terminalPhoneNumber) {
        this.msgId = msgId;
        this.key = key;
        this.vendorId = vendorId;
        this.terminalPhoneNumber = terminalPhoneNumber;
    }



    /**
     * 把  808头 透传消息类型 透传消息头 都属于消息头
     * @return 头部字节数组 = 808头 + 透传消息类型+透传消息头
     */
    @Override
    protected byte[] createMsgHeader(int bodySize) {
        // 数据类型0xf1+ 透传消息头+透传消息内容
        return createJTT808Header(bodySize);
    }

    private byte[] createTransHeader(int transBodySize) {
        int index = 0;
        byte[] transHeader = new byte[14];
        // 消息ID
        byte[] msgIdBytes = Tools.intTo2Bytes(msgId);
        transHeader[index++] =msgIdBytes[0];
        transHeader[index++] = msgIdBytes[1];
        // 流水号
        byte[] msgNumBytes = Tools.intTo2Bytes(frameFlowNum);
        transHeader[index++] = msgNumBytes[0];
        transHeader[index++] = msgNumBytes[1];
        // 厂商ID号
        byte[] vendorIdBytes = Tools.intTo2Bytes(vendorId);
        transHeader[index++] = vendorIdBytes[0];
        transHeader[index++] = vendorIdBytes[1];
        // 消息项 2 byte 保留
        index += 2;
        byte[] encryptKey = Tools.intTo4Bytes(key);
        for(byte item : encryptKey) {
            transHeader[index++] = item;
        }

        byte[] dataSizeBytes = Tools.intTo2Bytes(transBodySize);
        for(byte b : dataSizeBytes) {
            transHeader[index++] = b;
        }
        return transHeader;
    }

    /**
     *  创建808 头
     * @param bodySize = 透传消息类型+透传消息头 + 透传消息内容
     * @return
     */
    private byte[] createJTT808Header(int bodySize) {
        byte[] header = new byte[12];
        int index = 0;
        // 消息id 2 byte
        byte[] msgIdBytes = Tools.intTo2Bytes(0x900);
        header[index++] = msgIdBytes[0];
        header[index++] = msgIdBytes[1];

        // 消息体属性 2 byte
        byte[] bodySizeBytes = Tools.intTo2Bytes(bodySize);
        header[index++] = bodySizeBytes[0];
        header[index++] = bodySizeBytes[1];

        // 电话号码BCD码 6 byte
        byte[] phoneNumberBytes = Tools.getPhoneNumberBCD(terminalPhoneNumber);
        for(byte b : phoneNumberBytes) {
            header[index++] = b;
        }
        // 流水号 2 byte
        byte[] flowNumBytes = Tools.intTo2Bytes(frameFlowNum);
        header[index++] = flowNumBytes[0];
        header[index++] = flowNumBytes[1];
        // 消息封装项 根据消息体属性而定
        return header;
    }


    @Override
    public byte[] getMessage() {
        byte[] transBody = createMsgBody();
        int transBodySize = transBody.length;

        byte[] transHeader = createTransHeader(transBodySize);
        // bodySize = 透传消息类型 0xf1 + 透传消息头长度 + 透传消息体长度
        int bodySize = 1 + transHeader.length + transBodySize;
        byte[] jtt808Header = createMsgHeader(bodySize);
        // 帧长 = 808 头 + bodysize + 2*flag + 1 checksum
        int frameSize = jtt808Header.length + bodySize + 2 + 1;

        byte[] frameData = new byte[frameSize];

        int index= 0;
        // 开始标识符
        frameData[index++] = FLAG;
        for (byte b : jtt808Header) {
            frameData[index++] = b;
        }
        // 透传消息类型
        frameData[index++] = (byte)0xF1;

        // 透传消息头
        for (byte b : transHeader) {
            frameData[index++] = b;
        }

        // 透传消息体
        byte[] encryptBody = Tools.encrypt(key, transBody, transBody.length);
        for (byte b : encryptBody) {
            frameData[index++] = b;
        }

        // 校验
        byte checkSum = 0;
        for (byte b :  jtt808Header) {
            checkSum ^= b;
        }
        checkSum ^= 0xf1;
        for (byte b : transHeader) {
            checkSum ^= b;
        }
        for (byte b : transBody) {
            checkSum ^= b;
        }
        frameData[index++] = checkSum;
        //  结束标识符
        frameData[index++] = FLAG;
        return transformer(frameData);
    }
}
