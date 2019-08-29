package com.driving.application;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.driving.application.connect.ConnectManager;
import com.driving.application.fragment.RegisterFragment;
import com.driving.application.fragment.StudentLoginFragment;
import com.driving.application.fragment.TeacherLoginFragment;
import com.driving.application.fragment.UploadPicFragment;
import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.frame.HeartBeatFrame;
import com.driving.application.util.Logger;
import com.driving.application.util.PrefsUtil;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements Callback, RegisterFragment.ValidateListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpFragment();
        //600059737
        PrefsUtil.create(getApplicationContext());
//        byte[] buffer = new byte[]{0x26, 0x65, (byte)0xa1, (byte)0xce, 0x69, (byte)0xe7, (byte)0xa8, 0x6d, 0x38, 0x71, 0x24, 0x7c};
//        byte[] encrypt = Tools.encrypt(27496,  buffer, buffer.length);
//        byte[] buffer2 = new byte[]{0x01, 0x19, (byte)0xc4, (byte) 0xf8, 0x35, 0x0c, (byte) 0xe0, 0x4e, 0x1b, 0x3c, (byte)0xd6, 0x70};
//        Logger.i(Tools.bytesToHexString(encrypt));
//        byte[] encrypt2= Tools.encrypt(27496,  buffer2, buffer2.length);
//        Logger.i(Tools.bytesToHexString(encrypt2));

        byte[] buffer3 = new byte[]{0x01, 0x19, 0x08, (byte)0xa8, 0x17};
        byte[] encrypt3 = Tools.encrypt(27496,  buffer3, buffer3.length);
        Logger.i(Tools.bytesToHexString(encrypt3));
        byte[] buffer4 = new byte[]{0x26, 0x65, 0x6d, (byte)0xe9, 0x4b};
        byte[] encrypt4 = Tools.encrypt(27496,  buffer4, buffer4.length);
        Logger.i(Tools.bytesToHexString(encrypt4));
    }

    private void setUpFragment() {
        RegisterFragment tlf = RegisterFragment.newInstance("", "");
        tlf.setCallback(this);
        tlf.addValidateListener(this);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.fragment_container, tlf);
        t.commit();
    }


    @Override
    public void onNext(String jumpTo) {
        if("TeacherLoginFragment".equals(jumpTo)) {
            TeacherLoginFragment tlf = TeacherLoginFragment.newInstance("", "");
            tlf.setCallback(this);
            switchFragment(tlf);
        } else if("StudentLoginFragment".equals(jumpTo)) {
            StudentLoginFragment slf = StudentLoginFragment.newInstance("", "");
            slf.setCallback(this);
            switchFragment(slf);
        } else if("UploadPicFragment".equals(jumpTo)) {
            UploadPicFragment upf = UploadPicFragment.newInstance("", "");
            upf.setCallback(this);
            switchFragment(upf);
        }
    }


    @Override
    public void onValidate() {
        final JT808StFrame hbFrame = new HeartBeatFrame(Utils.TERMINAL_PHONE_NUMBER);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Logger.i("===================================");
                ConnectManager.getInstance().sendData(hbFrame.getMessage());
            }
        };
        timer.schedule(timerTask, 100, 30*1000);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.fragment_container, fragment);
        t.commit();
        t.addToBackStack("StudentLoginFragment");
    }
}
