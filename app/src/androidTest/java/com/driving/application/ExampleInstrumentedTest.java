package com.driving.application;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

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
        int key = 1000; //Tools.byte2Int(new byte[]{0x00, 0x00, 0x4d, 0x68});
        Logger.i("--------------------------"+key);
        //byte[] buffer = new byte[]{(byte)0xcd, (byte)0x98, (byte)0xc9, (byte)0x8b, 0x68, (byte)0xb7, 0x5c, 0x59, 0x54, 0x5f, 0x54, (byte)0xe7};
        byte[] original = {
                0x00, 0x19, 0x08, 0x29, 0x10, 0x00, 0x36, 0x35, 0x33, 0x30,
                0x31, 0x32, 0x37, 0x31, 0x39, 0x38, 0x35, 0x30, 0x39, 0x32,
                0x38, 0x31, 0x37, 0x31, 0x30, 0x03, (byte)0xc2, 0x66, (byte)0xd0, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x28, (byte)0xde,
                (byte)0xb2, 0x19, 0x08, 0x29, 0x10, 0x40, 0x16, 0x06, 0x1e, (byte)0xe2,
                (byte)0x8b, 0x01, 0x7d, (byte)0xa2, (byte)0x9e, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x20};
        byte[] buffer2 = new byte[]{0x79, (byte)0xda, (byte)0xf7, 0x3e, (byte)0x85, (byte)0xb0, (byte)0xa2, (byte)0xe7, (byte)0xf7, 0x4b, (byte)0x88, 0x68};
        int key2 = Tools.byte2Int(new byte[]{0x00, 0x00, 0x17, (byte)0xe7});
        // 2e 6c=11884
        // 加密
        byte[] encrypt = Tools.encrypt(key2, buffer2, buffer2.length);
        Logger.i(Tools.bytesToHexString(encrypt));
        // 解密
        byte[] encrypt2 = Tools.encrypt(key2, encrypt, encrypt.length);
        Logger.i(Tools.bytesToHexString(encrypt2));
        byte[] encrypt3 = Tools.encrypt(key2, encrypt2, encrypt2.length);
        Logger.i(Tools.bytesToHexString(encrypt3));
    }
}
