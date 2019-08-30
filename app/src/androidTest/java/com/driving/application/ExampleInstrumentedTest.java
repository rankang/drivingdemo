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
        byte[] buffer2 = new byte[]{(byte)0xf6, (byte)0xa2, (byte)0xa5, (byte)0xe0, (byte)0x8f, (byte)0x96, 0x53, (byte)0xee, 0x7b, 0x5d, (byte)0x8a, 0x3a};
        int key2 = Tools.byte2Int(new byte[]{0x00, 0x00, 0x6a, (byte)0xcf});
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
}
