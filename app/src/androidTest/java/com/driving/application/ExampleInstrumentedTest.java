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
        int key = Tools.byte2Int(new byte[]{0x00, 0x00, 0x4d, 0x68});
        Logger.i("--------------------------"+key);
        byte[] buffer = new byte[]{(byte)0xcd, (byte)0x98, (byte)0xc9, (byte)0x8b, 0x68, (byte)0xb7, 0x5c, 0x59, 0x54, 0x5f, 0x54, (byte)0xe7};
        // 2e 6c=11884
        byte[] encrypt = Tools.encrypt(key, buffer, buffer.length);
        Logger.i(Tools.bytesToHexString(encrypt));
        byte[] encrypt2 = Tools.encrypt(key, encrypt, encrypt.length);
        Logger.i(Tools.bytesToHexString(encrypt2));
    }
}
