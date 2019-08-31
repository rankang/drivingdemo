package com.driving.application.jt808.frame;

import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Tools;

/**
 * 图片上传通信帧
 */
public class PictureUploadFrame extends JT808StFrame {
    private int picId;
    /**当前包号从1 开始编号*/
    private byte cPackageNum;
    /**总包数*/
    private byte totalPackageCount;
    /**通道ID*/
    private byte chanelId;
    /**多媒体数据包*/
    private byte[] picPackageData;
    public PictureUploadFrame(String terminalPhoneNumber,
                              int picId, byte totalPackageCount, byte cPackageNum,  byte chanelId, byte[] picPackageData) {
        super(MSGID.PICTURE_UPLOAD_REQUEST, terminalPhoneNumber, true,totalPackageCount, cPackageNum);
        this.picId = picId;
        this.cPackageNum = cPackageNum;
        this.totalPackageCount = totalPackageCount;
        this.chanelId = chanelId;
        this.picPackageData = picPackageData;
    }

    @Override
    protected byte[] createMsgBody() {
        byte[] transBody = new byte[7+picPackageData.length];
        int index= 0;
        byte[] picIdByteArray = Tools.intTo4Bytes(picId);
        for(byte b : picIdByteArray) {
            transBody[index++] = b;
        }
        transBody[index++] = cPackageNum;
        transBody[index++] = totalPackageCount;
        transBody[index++] = chanelId;
        for(byte b : picPackageData) {
            transBody[index++] = b;
        }
        return transBody;
    }
}
