package com.driving.application;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

/**
 * 数据发送类
 * 1. 此类负责数据发送
 * double check singleton
 */
public class JT808Sender {



    private volatile static JT808Sender mInstance;
    private OutputStream outputStream;
    private boolean isSending = false;
    private LinkedBlockingQueue<byte[]> MSG_QUEUE = new LinkedBlockingQueue<>();

    private JT808Sender() {}

    /**单例方法*/
    public JT808Sender getInstance() {
        if(mInstance == null) {
            synchronized (JT808Sender.class) {
                if(mInstance == null) {
                    mInstance = new JT808Sender();
                }
            }
        }
        return mInstance;
    }

    /**发送消息*/
    public void pushMsg(byte[] data) {
        MSG_QUEUE.add(data);
        if(!isSending) isSending = true;
    }

    /**发送线程*/
    class SenderThread implements Runnable{
        @Override
        public void run() {
            try {
                while (isSending) {
                    if(MSG_QUEUE.isEmpty() && outputStream != null) {
                        sleep(1000);
                        continue;
                    }
                    byte[] data = MSG_QUEUE.poll();
                    try {
                        if(data != null && data.length > 0) {
                            outputStream.write(data);
                            // 使用socket发送出去
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } finally {
                //TODO:
            }
        }
    }
}
