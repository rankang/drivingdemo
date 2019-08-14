package com.driving.application.jt808;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * JT808 协议帧基类
 * 协议采用大端模式(big-endian)
 * 标识位-消息头-消息体-校验码-标识位
 */
public class Frame {
    /**帧标识符*/
    private static final byte FLAG = 0x7E;
    private static final int PACKAGE_SIZE = 1024;
//    /**
//     *  数据长度	WORD = 2
//     *  描述 数据的长度
//     */
//    private byte[] dataLength = new byte[2];
//
//    /**
//     * 总共14个字节
//     * 消息ID	WORD
//     * 流水号	WORD
//     * 厂商ID	WORD
//     * 消息项	WORD
//     * 加密key	 DWORD
//     */
//    private byte[] msgHeader = new byte[12];
//    /***
//     * 数据
//     */
//    private byte[] data;
//
//
//
//    /**若校验码、消息头以及消息体中出现0x7e，则要进行转义处理，转义
//     规则定义如下*/
//    protected byte[] transformer() {
//        return new byte[2];
//    }


    /**
     * @param msgID  消息编号
     * @param msgFlowNum 流水号
     * @param vendorID 厂商ID
     * @param encryPtKey 加密key ps：encryptKey 长度只能是4个字节
     * @return 头部直接数组
     */
    public byte[] createMsgHeader(int msgID, int msgFlowNum, int vendorID, byte[] encryPtKey) {
        int index = 0;
        byte[] header = new byte[12];
        // msgId
        byte[] msgIdBytes = Tools.intTo2Bytes(msgID);
        header[index++] =msgIdBytes[0];
        header[index++] = msgIdBytes[1];
        // 流水号
        byte[] msgNumBytes = Tools.intTo2Bytes(msgFlowNum);
        header[index++] = msgNumBytes[0];
        header[index++] = msgNumBytes[1];
        // 厂商ID号
        byte[] vendorIdBytes = Tools.intTo2Bytes(vendorID);
        header[index++] = vendorIdBytes[0];
        header[index++] = vendorIdBytes[1];
        index += 2;
        for(byte item : encryPtKey) {
            header[index++] = item;
        }
        Logger.i("--header="+Tools.bytesToHexString(header));
        return header;
    }

    /**
     * @param data 数据
     * @return 开始位置
     */
    public byte[] createMsgBody(byte[] data, byte[] gpsPackage) {
        if(gpsPackage == null)  return data;
        // gps 包一般是30个字节
        byte[] tempData = new byte[data.length+gpsPackage.length];
        int index = 0;
        for(byte item : data) {
            tempData[index++] = item;
        }
        for(byte item : gpsPackage) {
            tempData[index++] = item;
        }
        Logger.i("--body="+Tools.bytesToHexString(tempData));
        return tempData;
    }

    /**
     * 构建gps数据包
     * @param lat 纬度
     * @param lng 经度
     * @param height  高度
     * @param speed 速度
     * @param recordDevSpeed 记录仪速度
     * @param direction 记录仪速度
     * @param status1 状态1
     * @param status2 状态 JT808状态定义
     * @return 字节数据
     */
    public byte[] createGpsPackage(int lat, int lng, int height, int speed,
                            int recordDevSpeed, int direction, int status1, int status2) {

        int index = 0;
        // 初始化数据包 30 byte
        byte[] gpsData = new byte[30];
        // 时间BCD 码
        String datetime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).format(new Date());
        Logger.i(datetime);
        byte[] bcdDateTime = Tools.getBCDByteArray(datetime);
        for(int i=0; i<bcdDateTime.length; i++) {
            gpsData[index++] = bcdDateTime[i];
        }
        // 经度
        byte[] lngBytes = Tools.intTo4Bytes(lng);
        for(byte b : lngBytes) {
            gpsData[index++] = b;
        }
        // 纬度
        byte[] latBytes = Tools.intTo4Bytes(lat);
        for(byte b : latBytes) {
            gpsData[index++] = b;
        }

        // 高度
        byte[] heightBytes = Tools.intTo2Bytes(height);
        gpsData[index++] = heightBytes[0];
        gpsData[index++] = heightBytes[1];

        // 速度
        byte[] speedBytes = Tools.intTo2Bytes(speed);
        gpsData[index++] = speedBytes[0];
        gpsData[index++] = speedBytes[1];

        // 记录设备速度
        byte[] recordDevSpeedBytes = Tools.intTo2Bytes(recordDevSpeed);
        gpsData[index++] = recordDevSpeedBytes[0];
        gpsData[index++] = recordDevSpeedBytes[1];

        // 方向 0-359度,正北0,顺时针
        byte[] directionBytes = Tools.intTo2Bytes(direction);
        gpsData[index++] = directionBytes[0];
        gpsData[index++] = directionBytes[1];
        // 状态1 JT808状态定义
        byte[] status1Bytes = Tools.intTo4Bytes(status1);
        for(byte b : status1Bytes) {
            gpsData[index++] = b;
        }
        // 状态2
        byte[] status2Bytes = Tools.intTo4Bytes(status2);
        for(byte b : status2Bytes) {
            gpsData[index++] = b;
        }
        Logger.i("gps="+Tools.bytesToHexString(gpsData));
        return gpsData;
    }

    /**
     * 帧数据构建
     * @param header 消息头部
     * @param body 消息体数据
     * @return
     */
    public byte[] getFrameData(byte[] header, byte[] body) {
        // header 12,+数据长度+2x标识符+dataLength 2个字节+ checkSum 1个字节
        byte[] frameData = new byte[12+body.length+2+2+1];
        int index= 0;
        // 开始标识符
        frameData[index++] = FLAG;
        // header
        for(int i=0; i<header.length; i++) {
            frameData[index++] = header[i];
        }
        byte[] dataSize = Tools.intTo2Bytes(body.length);
        for(int i=0; i<dataSize.length; i++) {
            frameData[index++] = dataSize[i];
        }
        // data 赋值
        for(int i=0; i<body.length; i++) {
            frameData[index++] = body[i];
        }

        // checkSum 计算
        byte checkSum = header[0];
        for(int i=1; i<header.length; i++) {
            checkSum = (byte) (checkSum ^ header[i]);
        }
        for(byte dataItem : body) {
            checkSum = (byte) (checkSum ^ dataItem);
        }
        frameData[index++] = checkSum;

        // 结束标识符
        frameData[index] = FLAG;
        Logger.i("frameData="+Tools.bytesToHexString(frameData));
        return frameData;
    }


    /**
     * transformer
     * 采用Ox7e表示，若校验码、消息头以及消息体中出现0x7e，则要进行转义处理，转义
     * 转义处理过程如下:
     *发送消息时:消息封装——>计算并填充校验码——>转义;
     *接收消息时:转义还原——>验证校验码——>解析消息。
     */



//    private byte[] transformer(byte[] frameData) {
//
//    }



}
