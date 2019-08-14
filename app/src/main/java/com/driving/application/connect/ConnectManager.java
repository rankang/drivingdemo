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

    private ConnectManager() {}

    public static ConnectManager getInstance() {
        if(mInstance == null) {
            synchronized (ConnectManager.class) {
                if(mInstance == null) mInstance = new ConnectManager();
            }
        }
        return mInstance;
    }


    /**使用tcp短链接异步发送请求*/
    public void sendData(byte[] data) {
        new Thread(new SenderThread(data)).start();
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
                //dis.close();
                //socket.shutdownInput();
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
