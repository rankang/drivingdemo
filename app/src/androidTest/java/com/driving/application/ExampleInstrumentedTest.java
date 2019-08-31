package com.driving.application;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.driving.application.connect.ConnectManager;
import com.driving.application.jt808.JT808StFrame;
import com.driving.application.util.Logger;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.driving.application", appContext.getPackageName());
    }

    @Test
    public void testEncrypt() {
        byte[] buffer2 = new byte[]{0x19, 0x08, 0x31, 0x12, 0x00, 0x00, 0x01};
        int key2 = 1;
       // int key2 = Tools.byte2Int(new byte[]{0x00, 0x00, 0x55, (byte)0x86});
        // 2e 6c=11884
        // 加密
        byte[] encrypt = Tools.encrypt(key2, buffer2, buffer2.length);
        Logger.i(Tools.bytesToHexString(encrypt));
        String d = new String(new byte[]{(byte)0xee, 0x66, 0x76, (byte)0xef, (byte)0xe8, 0x45});
        Logger.i(d);
        // 解密
        byte[] encrypt2 = Tools.encrypt(key2, encrypt, encrypt.length);
        Logger.i(Tools.bytesToHexString(encrypt2));
        byte[] encrypt3 = Tools.encrypt(key2, encrypt2, encrypt2.length);
        Logger.i(Tools.bytesToHexString(encrypt3));
    }

    @Test
    public void testTransformback() {
        byte[] transFromer = new byte[]{0x7e, (byte)0x89, 0x00, 0x00, 0x1b, 0x01, (byte)0x84, 0x69, 0x12, 0x73, 0x02, (byte)0x80,
                (byte)0xa0, (byte)0xf1, (byte)0x81, 0x01, 0x6a, (byte)0xd6, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x27, 0x02, 0x00, 0x0c,
                (byte)0x8e, 0x48, (byte)0xf3, 0x5f, 0x49, 0x6e, 0x7d, 0x02, (byte)0xea, (byte)0xf3, (byte)0xf9, (byte)0xc7, (byte)0x8a, 0x42, 0x7e};
        byte[] originByte = ConnectManager.transformerBack(transFromer);
        Logger.i(Tools.bytesToHexString(originByte));
    }

    @Test
    public void testBitHandle() {
        int num=123; //初始值，也是商值
       //JT808StFrame.createMsgBodyAttr(num, true);
    }
}
