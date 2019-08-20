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

public class ConnectManager {
    /**云南昆明运管*/
    private static final String TCP_YG_IP_YN_KM = "139.9.0.128";
    private static final int TCP_YG_PORT_YN_KM = 9607;

    private volatile static ConnectManager mInstance;
    private InputStream inStream;
    private OutputStream outStream;
    private Socket clientSocket;

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
        final byte[] temp1 = {0x7e, 0x01, 0x00, 0x00, 0x21, 0x01, 0x47, (byte) 0x85, 0x23, 0x69, 0x00, 0x00, 0x46, 0x00, 0x0B, 0x04, 0x57, 0x53,
                0x31, 0x30, 0x30, 0x30, 0x53, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x32, 0x7c, (byte)0xa4,
                0x42, 0x50, 0x39, 0x34, 0x4a, 0x35, (byte) 0xef, 0x7e};
        final byte[] temp = {0x7e, 0x01, 0x02, 0x00, 0x06, 0x01, 0x47, (byte) 0x85, 0x23, 0x69, 0x00, 0x00, 0x45, 0x61, 0x61, 0x61, 0x61, 0x61, 0x61, (byte) 0xef, 0x7e};

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    if(clientSocket == null) {
                        init();
                    }
                   // OutputStream outStream = clientSocket.getOutputStream();
                   // DataOutputStream dos = new DataOutputStream(outStream);
                    outStream.write(temp1);
                    outStream.flush();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }.start();


    }


    public void read() {
        if(clientSocket == null) {
            init();
        }
        new Thread(new ReadThread()).start();
    }



    public class ReadThread implements Runnable {
        @Override
        public void run() {
            try {
               //InputStream inStream = clientSocket.getInputStream();
                //DataInputStream dis = new DataInputStream(inStream);
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inStream.read(buffer)) != -1 ) {
                    Logger.i("----------------------------------");
                    Logger.i(Tools.bytesToHexString(buffer));
                }
                Logger.i("+++++++++++++++++++++++++++++++");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


    public boolean connectJTServer(String ip, int port) {
        try {
            Socket clientSocket = new Socket(ip, port);
            //clientSocket.setKeepAlive(true);
            InetSocketAddress sa = new InetSocketAddress(ip, port);
            clientSocket.connect(sa);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public void init() {
        try {
            clientSocket= new Socket(TCP_YG_IP_YN_KM, TCP_YG_PORT_YN_KM);
            inStream = clientSocket.getInputStream();
            outStream = clientSocket.getOutputStream();
            read();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }





    /**发送数据线程*/
    public class SenderThread implements Runnable {
        byte[] data;
        public SenderThread(byte[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket(TCP_YG_IP_YN_KM, TCP_YG_PORT_YN_KM);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputStream);
                // 数据是否需要分包
                dos.write(data);
                dos.flush();
                socket.shutdownOutput();
                try {
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] buffer = new byte[1024];
                InputStream inputStream = socket.getInputStream();
                DataInputStream dis = new DataInputStream(inputStream);
                int len = -1;
                while ((len = dis.read(buffer)) != -1) {
                    //dis.read(buffer, 0, len);
                    Logger.i(Tools.bytesToHexString(buffer));
                    // 需要计算校验
                }

                dos.close();
                outputStream.close();
                dis.close();
                inputStream.close();
                socket.close();
            } catch (IOException ioe) {
                Logger.i("--catch an IOException--");
                ioe.printStackTrace();
            } finally {

            }

        }
    }

    /**接收数据线程*/
    public class ReceiverThread implements Runnable {
        @Override
        public void run() {

        }
    }









}
