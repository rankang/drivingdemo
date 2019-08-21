package com.driving.application.connect;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

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
    private ConnectManager() {

    }

    public static ConnectManager getInstance() {
        if(mInstance == null) {
            synchronized (ConnectManager.class) {
                if(mInstance == null) mInstance = new ConnectManager();
            }
        }
        return mInstance;
    }




    /**使用tcp短链接异步发送请求*/
    public void sendData(final byte[] data) {
        //new Thread(new SenderThread(data)).start();
        // 注册
        // 530101 000000 取前两位和后4位-> 00 35 00 00
        // (0X02) 车牌颜色
        ///////////////////手机及车牌///////////////////
        // 14736486727云D5285学
        //18469127302 云A5300学
        // 注册结果返回
        //7e
        // 81 00
        // 00 0c
        // 01 84 69 12 73 02
        // 00 46
        // 00
        // 46 00 36 30 30 30 35 39 37 33 37
        // 3b 7e
        final byte[] temp = {
                0x7e, // 标识
                0x01, 0x00, // 消息ID
                0x00, 0x22, // 消息体属性
                0x01, (byte)0x84, (byte) 0x69, 0x12, 0x73, 0x02, // 终端手机号BCD码 = 6
                0x00, 0x46, // 消息流水号 = 2
                0x00, 0x35,  // 省ID = 2
                0x00, 0x00,  // 市ID =2
                0x53, 0x31, 0x30, 0x30,  0x30, // 制造商ID =5
                0x53, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,  // 终端型号 = 8
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,  // 终端ID = 7
                0x02, // 车牌颜色 = 1
                (byte)0xd4, (byte)0xc6, (byte)0x41, 0x35, 0x33, 0x30, 0x30, (byte)0xd1, (byte)0xA7, // 车牌号 = 9
                (byte)0xfe, // 异或校验
                0x7e
        };

        // 鉴权
        //final byte[] temp = {0x7e, 0x01, 0x02, 0x00, 0x06, 0x01, 0x47, (byte) 0x85, 0x23, 0x69, 0x00, 0x00, 0x45, 0x61, 0x61, 0x61, 0x61, 0x61, 0x61, (byte)0xC9, 0x7e};
        // 计算校验码
        byte checkSum = temp[1];
        for(int i=2; i < temp.length-2; i++) {
            checkSum ^= temp[i];
            Logger.i(Tools.byteToHexString(temp[i]));
        }
        Logger.i("--------------------------------------");
        Logger.i(Tools.byteToHexString(checkSum));
        Logger.i(Tools.bytesToHexString(temp));
        sendImpl(temp);
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if(clientSocket == null) {
//                        inStream = null;
//                        outStream = null;
//                        init();
//                    }
//                    outStream.write(temp);
//                    outStream.flush();
//                } catch (IOException ioe) {
//                    ioe.printStackTrace();
//                }
//            }
//        });
    }


    public void sendImpl(final byte[] data) {
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


    public void readData() {
        if(clientSocket == null) {
            init();
        }
        new Thread(new ReadThread()).start();
    }

    public class ReadThread implements Runnable {
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                int len = -1;
                while (!clientSocket.isClosed()) {
                    inStream.read(buffer);
                    Logger.i(Tools.bytesToHexString(buffer));
                    Logger.i("+++++++++++++++++++++++++++++++");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
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
}
