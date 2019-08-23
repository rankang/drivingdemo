package com.driving.application.jt808;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

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
    protected byte[] createMsgHeader(int transBodySize) {
        //
        int bodySize = 1 + 14 + transBodySize;
        byte[] jt808Header = createJTT808Header(bodySize);

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
        int jt808HeaderSize = jt808Header.length;
        int transHeaderSize = transHeader.length;
        // jt808HeaderSize + transHeaderSize + 透传消息类型 1byte
        index = 0;
        byte[] header = new byte[jt808HeaderSize+transHeaderSize+1];
        for(byte b : jt808Header) {
            header[index++] = b;
        }
        header[index++] = (byte) 0xF1;
        for(byte b : transHeader) {
            header[index++] = b;
        }
        return header;
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
        byte[] msgIdBytes = Tools.intTo2Bytes(0x0900);
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
        Logger.i("------------transBody---------"+Tools.bytesToHexString(transBody));
        int transBodySize = transBody.length;
        byte[] header = createMsgHeader(transBodySize);

        // header size,+数据长度+2x标识符+ checkSum 1个字节
        byte[] frameData = new byte[header.length+transBody.length+2+1];

        int index= 0;
        // 开始标识符
        frameData[index++] = FLAG;

        // header
        for (byte b : header) {
            frameData[index++] = b;
        }

        // 消息体 加密
        Logger.i("+++++++++++++++++before+++++++++++++++++++++"+Tools.bytesToHexString(transBody));
        byte[] encryptBody = Tools.encrypt(key, transBody, transBody.length);
        Logger.i("++++++++++++++++++after+++++++++++++++++++++"+Tools.bytesToHexString(encryptBody));
        for (byte b : encryptBody) {
            frameData[index++] = b;
        }
//        for(byte b : transBody) {
//            frameData[index++] = b;
//        }
        // checkSum 计算
        byte checkSum = header[0];
        for(int i=1; i<header.length; i++) {
            checkSum ^=  header[i];
        }
        for(byte b : encryptBody) {
            checkSum ^= b;
        }

        frameData[index++] = checkSum;
        Logger.i("------------------checkSum1-------------------------"+Tools.byteToHexString(checkSum));
        // 结束标识符
        frameData[index] = FLAG;
        Logger.i("+++++++++frameData="+Tools.bytesToHexString(frameData));
        return transformer(frameData);
    }
}
