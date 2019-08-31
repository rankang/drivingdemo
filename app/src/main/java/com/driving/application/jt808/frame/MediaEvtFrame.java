package com.driving.application.jt808.frame;

import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaEvtFrame extends JT808StFrame {
    private int mediaId;
    private byte mediaType;
    private byte mediaEncode;
    private byte evtCode;
    private byte chanelId;
    private int teacherId;
    private int stuId;
    private int schoolNum;
    private byte[] stuLoginNum;
    private int reverse;
    public MediaEvtFrame(String phoneNumber, int mediaId, byte mediaType, byte mediaEncode,
                         byte evtCode, byte chanelId, int teacherId, int stuId, int schoolNum, byte[] stuLoginNum, int reverse) {
        super(MSGID.PICTURE_EVT_REQUEST, phoneNumber);
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.mediaEncode = mediaEncode;
        this.evtCode = evtCode;
        this.chanelId = chanelId;
        this.teacherId = teacherId;
        this.stuId = stuId;
        this.schoolNum = schoolNum;
        this.stuLoginNum = stuLoginNum;
        this.reverse = reverse;
    }

    @Override
    protected byte[] createMsgBody() {
        byte[] body = new byte[38];
        int index = 0;
        // 多媒体ID
        byte[] mediaIdByteArray = Tools.intTo4Bytes(mediaId);
        for (byte b : mediaIdByteArray) {
            body[index++] = b;
        }
        // 多媒体类型 0：图像；
        body[index++] = mediaType;
        //0：JPEG 其他保留
        body[index++] = mediaEncode;
        // 事件项编码
        body[index++] = evtCode;
        //通道ID
        body[index++] = chanelId;
        // 教练ID
        byte[] teacherIdByteArray = Tools.intTo4Bytes(teacherId);
        for (byte b : teacherIdByteArray) {
            body[index++] = b;
        }
        // 学员ID
        byte[] stuIdByteArray = Tools.intTo4Bytes(stuId);
        for (byte b :  stuIdByteArray) {
            body[index++] = b;
        }
        // 驾校编号
        byte[] schoolNumByteArray = Tools.intTo4Bytes(schoolNum);
        for (byte b : schoolNumByteArray) {
            body[index++] = b;
        }
        // 学员登录编号
        for(byte b : stuLoginNum) {
            body[index++] = b;
        }
        // 拍照时间
        String time = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(new Date());
        byte[] timeByteArray = Tools.getBCDByteArray(time);
        for(byte b : timeByteArray) {
            body[index++] = b;
        }
        // 保留
        byte[] reverseByteArray = Tools.intTo4Bytes(reverse);
        for (byte b : reverseByteArray) {
            body[index++] = b;
        }
        return body;
    }
}
