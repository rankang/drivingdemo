package com.driving.application.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectManager {

    private volatile static ConnectManager mInstance;
    private InputStream inStream;
    private OutputStream outStream;
    private Socket clientSocket;

    private ConnectManager() {}

    public ConnectManager getInstance() {
        if(mInstance == null) {
            synchronized (ConnectManager.class) {
                if(mInstance == null) mInstance = new ConnectManager();
            }
        }
        return mInstance;
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









}
