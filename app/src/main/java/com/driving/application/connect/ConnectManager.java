package com.driving.application.connect;

import com.driving.application.event.EvtBusEntity;
import com.driving.application.jt808.BaseFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

import org.greenrobot.eventbus.EventBus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectManager {
    /**云南昆明运管*/
    private static final String TCP_YG_IP_YN_KM = "139.9.0.128";
    private static final int TCP_YG_PORT_YN_KM = 9607;

    private volatile static ConnectManager mInstance;
    private InputStream inStream;
    private OutputStream outStream;
    private Socket clientSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private ConnectManager() {}

    public static ConnectManager getInstance() {
        if(mInstance == null) {
            synchronized (ConnectManager.class) {
                if(mInstance == null) mInstance = new ConnectManager();
            }
        }
        return mInstance;
    }

    Object emptyObject = new Object();
    public void init() {
        try {
            if(clientSocket == null) {
                synchronized (emptyObject) {
                    if(null == clientSocket) {
                        clientSocket= new Socket(TCP_YG_IP_YN_KM, TCP_YG_PORT_YN_KM);
                    }
                }
                inStream = clientSocket.getInputStream();
                outStream = clientSocket.getOutputStream();
                readData();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



    /**使用tcp短链接异步发送请求*/
    public void sendData(final byte[] data) {
           // 计算校验码
        byte checkSum = data[1];
        for(int i=2; i < data.length-2; i++) {
            checkSum ^= data[i];
        }
        Logger.i("-----------------sendData---------------------");
        Logger.i(Tools.byteToHexString(checkSum));
        Logger.i(Tools.bytesToHexString(data));
        sendImpl(data);
    }


    private void sendImpl(final byte[] data) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if(clientSocket == null) {
                        inStream = null;
                        outStream = null;
                        init();
                    }
                    outStream.write(data);
                    outStream.flush();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });
    }


    private void readData() {
        if(clientSocket == null) {
            init();
        }
        new Thread(new ReadThread()).start();
    }

    private class ReadThread implements Runnable {
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                while (!clientSocket.isClosed()) {
                    int len = inStream.read(buffer);
                    if(len > 0) {
                        byte[] receivedData = new byte[len];
                        System.arraycopy(buffer, 0, receivedData, 0, len);
                        Logger.i("++++++++++++++++received data+++++++++++++++");
                        Logger.i(Tools.bytesToHexString(receivedData));
                        // 分析数据
                        analysis(receivedData);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


    private int pRd = 0; // 读的位置
    private int pWr = 0; // 写的位置
    /**开辟2K的接收缓冲区*/
    private byte[] mRevBuffer = new byte[2048];
    private boolean isAnalysis = false;
    /**
     * 解析JT808协议
     * @param data 每次收到数据
     *  接收消息时:转义还原——>验证校验码——>解析消息。
     */
    private void analysis(byte[] data) {
        int len = data.length;
        System.arraycopy(data, 0, mRevBuffer, pWr, len);
        pWr += len;
        if(isAnalysis) return;
        // 当读的位置小于写的位置，并且pwr 的位置大于12（即通信帧的最小长度）
        while (pRd < pWr && pWr > 12) {
            isAnalysis = true;
            // 找帧头
            int flag = 0;
            while (mRevBuffer[pRd] != 0x7e) {
                pRd++;
                if(pWr<pRd) {
                    pRd = 0;
                    pWr = 0;
                    flag = 1;
                    break;
                }
            }
            if(flag == 1) break;
            flag = 0;
            // 找到第一个0x7e，再找到结束的0x7e
            int secFlagPosition = pRd+1;
            while (mRevBuffer[secFlagPosition] != 0x7e) {
                secFlagPosition ++;
                // 如果没找到第二个0x7e, 可能还没接受完
                if(secFlagPosition > pWr) {
                    pRd = 0;
                    pWr = 0;
                    flag = 1;
                    break;
                }
            }

            if(flag == 1) break;

            // 找到第二个0x7e 截取数据
            int dataLength = secFlagPosition - pRd + 1;
            byte[] msgData = new byte[dataLength];
            for(int i=0; i< dataLength; i++) {
                msgData [i] = mRevBuffer[i+pRd];
            }
            // transformback
            Logger.i("transformer-data==========="+Tools.bytesToHexString(msgData));
            byte[] originalData = transformerBack(msgData);
            Logger.i("original-data=================="+Tools.bytesToHexString(originalData));
            //根据
            // 计算checkSum
            byte calculatedCheckSum = Tools.checkSum(originalData, 1, originalData.length-3);
            byte checkSum = originalData[originalData.length-2];
            Logger.i("calculatedCheckSum===="+Tools.byteToHexString(calculatedCheckSum));
            Logger.i("checkSum===="+Tools.byteToHexString(checkSum));
            // 校验通过处理数据
            if(calculatedCheckSum == checkSum) {
                dataProcess(originalData);
            }

            int frameLength = originalData.length;
            pRd += frameLength;
            if(pRd < pWr) {
                byte[] buf = new byte[2048];
                System.arraycopy(mRevBuffer, pRd, buf, 0, pWr - pRd);
                mRevBuffer = buf;
                pWr -= pRd;
                pRd = 0;
            } else if(pWr == pRd) {
                mRevBuffer = new byte[2048];
                pWr = 0;
                pRd = 0;
            }
        }
        isAnalysis = false;
    }


    /**
     *  把数据转回来 0x7d 0x02 -> 0x7e, 0x7d 0x01->0x7d
     *  将首尾的flag 0x7e去掉
     * @param transformBytes
     * @return
     */
    private byte[] transformerBack(byte[] transformBytes) {
        int count = 0;
        byte[] temp = new byte[transformBytes.length-2];
        for(int i=1; i<transformBytes.length-1; i++) {
            if(transformBytes[i] == 0x7d && (transformBytes[i+1] == 0x01 || transformBytes[i+1] == 0x02)) {
                count++;
            }
            temp[i-1] = transformBytes[i];
        }
        if(count <= 0) return transformBytes;

        byte[] originBytes = new byte[transformBytes.length - count];
        originBytes[0] = transformBytes[0];
        originBytes[0] = 0x7e;
        for(int i=0; i<temp.length; i++) {
            if(temp[i] == 0x7d && (i+1) < temp.length &&  temp[i+1] == 0x01) {
                originBytes[i] = 0x7d;
                i++;
            } else if(temp[i] == 0x7d && (i+1) < temp.length  && temp[i+1] == 0x02) {
                originBytes[i] = 0x7e;
                i++;
            } else {
                originBytes[i] = temp[i];
            }
        }
        originBytes[originBytes.length-1] = 0x7e;
        return originBytes;
    }


    private void dataProcess(byte[] data) {
        // 在次检测
        if(data[0] != 0x7e || data[data.length-1] != 0x7e) {
            return;
        }
        int len = data.length;
        int msgId = Tools.twoBytes2Int(new byte[]{data[1], data[2]});
        byte[] msgData = null;
        switch (msgId) {
            // Standard JTT808
            case MSGID.COMMON_RES:
            case MSGID.REGISTER_RES :
                // 暂无其他属性，只标识数据长度
                int msgBodyAttr = Tools.twoBytes2Int(new byte[]{data[3], data[4]});
                // 根据数据长度取数据
                int dataEndIndex = len-3;
                int dataStartIndex = dataEndIndex-msgBodyAttr+1;
                msgData = new byte[msgBodyAttr];
                for(int i=dataStartIndex; i<=dataEndIndex; i++) {
                    msgData[i-dataStartIndex] = data[i];
                }
                Logger.i(Tools.bytesToHexString(msgData));
                break;
        }
        EventBus.getDefault().post(new EvtBusEntity(msgId, msgData));
    }

}
