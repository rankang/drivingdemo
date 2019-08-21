package com.driving.application.jt808;

import com.driving.application.util.Tools;

/**
 * JT/T808 标准协议封装
 */
public class JT808StFrame extends BaseFrame {
    /**
     *0	     消息ID	WORD
     * 2  	消息体属性	WORD	消息体属性格式结构图见图2
     * 4	     终端手机号	BCD[6]	根据安装后终端自身的手机号转换。手机号不足12位，则在前补充数字，大陆手机
     *          号补充数字0港澳台则根据其区号进行位数补充。
     * 10	消息流水号	WORD	按发送顺序从0开始循环累加
     * 12	消息包封装项		如果消息体属性中相关标识位确定消息分包处理，则该项有内容，否则无该项
     * @param msgId  消息ID
     * @param phoneNumber 电话号码
     * @return  message header 字节数组
     */
    public byte[] createMsgHeader(int msgId, String phoneNumber, int bodySize) {
        byte[] header = new byte[12];
        int index = 0;
        // 消息id 2 byte
        byte[] msgIdBytes = Tools.intTo2Bytes(msgId);
        header[index++] = msgIdBytes[0];
        header[index++] = msgIdBytes[1];

        // 消息体属性 2 byte
        byte[] bodySizeBytes = Tools.intTo2Bytes(bodySize);
        header[index++] = bodySizeBytes[0];
        header[index++] = bodySizeBytes[1];

        // 电话号码BCD码 6 byte
        byte[] phoneNumberBytes = Tools.getPhoneNumberBCD(phoneNumber);
        for(byte b : phoneNumberBytes) {
            header[index++] = b;
        }
        // 流水号 2 byte
        byte[] flowNumBytes = Tools.intTo2Bytes(getFlowNum());
        header[index++] = flowNumBytes[0];
        header[index++] = flowNumBytes[1];
        // 消息封装项 根据消息体属性而定
        return header;
    }

    /**
     *
     * @return
     */
    public byte[] createMsgData(byte[] header, byte[] body) {
        // body长度 + header 长度 + 2 个flag + 校验
        int length = body.length + header.length + 2 + 1;
        byte[] msgData = new byte[length];
        int index = 0;
        msgData[index++] = FLAG;
        for(byte b : header) {
            msgData[index++] = b;
        }
        for(byte b : body) {
            msgData[index++] = b;
        }
        byte checSum = Tools.checkSum(msgData, 1, header.length+body.length);
        msgData[index++] = checSum;
        msgData[index] = FLAG;
        return transformer(msgData);
    }
}
