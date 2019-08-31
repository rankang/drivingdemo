package com.driving.application.jt808;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

import java.math.BigInteger;

/**
 * JT/T808 标准协议封装
 */
public abstract class JT808StFrame extends BaseFrame {
    private int msgId;
    private String phoneNumber;
    private boolean isMultiPackage;
    /**消息包总数*/
    private int totalPackageCount;
    /**从1开始*/
    private int packageSequence;
    public JT808StFrame(int msgId, String phoneNumber) {
        this.msgId = msgId;
        this.phoneNumber = phoneNumber;
    }

    public JT808StFrame(int msgId, String phoneNumber, boolean isMultiPackage, int totalPackageCount, int packageSequence) {
        this(msgId, phoneNumber);
        this.isMultiPackage = isMultiPackage;
        this.totalPackageCount = totalPackageCount;
        this.packageSequence = packageSequence;
    }


    /**
     *0	消息ID	WORD
     * 2  	消息体属性	WORD	消息体属性格式结构图见图2
     * 4	     终端手机号	BCD[6]	根据安装后终端自身的手机号转换。手机号不足12位，则在前补充数字，大陆手机
     *          号补充数字0港澳台则根据其区号进行位数补充。
     * 10	消息流水号	WORD	按发送顺序从0开始循环累加
     * 12	消息包封装项		如果消息体属性中相关标识位确定消息分包处理，则该项有内容，否则无该项
     * @return  message header 字节数组
     */

    @Override
    protected byte[] createMsgHeader(int bodySize) {
        byte[] header;
        if(isMultiPackage) {
            header = new byte[16];
        } else {
            header =  new byte[12];
        }
        int index = 0;
        // 消息id 2 byte
        byte[] msgIdBytes = Tools.intTo2Bytes(msgId);
        header[index++] = msgIdBytes[0];
        header[index++] = msgIdBytes[1];

        // 消息体属性 2 byte
        byte[] bodySizeBytes =createMsgBodyAttr(bodySize, isMultiPackage);
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
        if(isMultiPackage) {
            byte[] totalPackageByteArray = Tools.intTo2Bytes(totalPackageCount);
            byte[] packageSequenceByteArray = Tools.intTo2Bytes(packageSequence);
            for(byte b : totalPackageByteArray) {
                header[index++] = b;
            }
            for(byte b : packageSequenceByteArray) {
                header[index++] = b;
            }
        }
        return header;
    }


    @Override
    public byte[] getMessage() {
        byte[] body = createMsgBody();
        int bodySize = body.length;
        byte[] header = createMsgHeader(bodySize);

        // body长度 + header 长度 + 2 个flag + 校验
        int length = body.length + header.length + 2 + 1;

        // 初始化msgdata字节数组
        byte[] msgData = new byte[length];
        int index = 0;
        msgData[index++] = FLAG;
        for(byte b : header) {
            msgData[index++] = b;
        }
        for(byte b : body) {
            msgData[index++] = b;
        }
        byte checkSum = Tools.checkSum(msgData, 1, header.length+body.length);
        msgData[index++] = checkSum;
        msgData[index] = FLAG;
        return transformer(msgData);
    }

    private byte[] createMsgBodyAttr(int bodySize, boolean byPackage) {
        if(!byPackage) return Tools.intTo2Bytes(bodySize);
        // 一共16位
        byte[] bitArray = new byte[16];
        String bitString = Integer.toBinaryString(bodySize);//integer2BitS(bodySize);
        Logger.i("bit String=="+bitString);
        // 将第二位设置为1,表示数据分包
        bitArray[2] = 0x01;
        int size = bitString.length();
        int start = 16-size;
        for(int i=start; i < 16; i++) {
            String numStr = String.valueOf(bitString.charAt(i-start));
            bitArray[i] = Byte.parseByte(numStr);
        }
        Logger.i(Tools.bytesToHexString(bitArray));
        String binStr = "";
        for(int i = 0; i < bitArray.length; i++) {
            binStr += String.valueOf(bitArray[i]);
        }
        int intValue = Integer.parseInt(binStr, 2);
        return Tools.intTo2Bytes(intValue);
    }
}
