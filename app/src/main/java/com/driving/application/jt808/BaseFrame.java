package com.driving.application.jt808;

/**
 * JT808 协议帧基类
 * 协议采用大端模式(big-endian)
 */
public class BaseFrame {
    /**帧标识符*/
    private Byte FLAG = 0X7e;

    /**消息头*/
    /**消息ID 用WORD表示，2个字节无符号数*/
    private short MSG_HEADER_ID =10;

    /**消息体属性, WORD */
    private short HEADER_MSG_ATTR =11;

    /**终端手机号或者设备ID号 BCD[6] 根据安装后终端自身的手机号转换。手机号不足12位，则在前补充数字，大陆手机
     号补充数字0港澳台则根据其区号进行位数补充*/
    private short HEADER_DEVICE_ID = 12;

    /**消息流水号 WORD 按发送顺序从0开始循环累加*/
    private short HEADER_MSG_SEQUENCE = 0;

    /**消息包封装项 如果消息体属性中相关标识位确定消息分包处理，则该项有内容，否则无该项,
     * WORD 描述
     * */
    private short HEADER_MSG_PACKAGE_COUNT = 11;

    /**
     * 包序号(word(16))  从 1 开始
     * */
    private short HEADER_MSG_PACKAGE_SEQUENCE = 1;

    /**若校验码、消息头以及消息体中出现0x7e，则要进行转义处理，转义
     规则定义如下*/
    protected byte[] transformer() {
        return new byte[2];
    }

}
