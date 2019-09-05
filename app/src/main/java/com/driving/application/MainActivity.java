package com.driving.application;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.driving.application.connect.ConnectManager;
import com.driving.application.fragment.LogoutFragment;
import com.driving.application.fragment.RegisterFragment;
import com.driving.application.fragment.StudentLoginFragment;
import com.driving.application.fragment.StudyDataFragment;
import com.driving.application.fragment.TeacherLoginFragment;
import com.driving.application.fragment.UploadPicFragment;
import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.frame.HeartBeatFrame;
import com.driving.application.util.Logger;
import com.driving.application.util.PrefsUtil;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;
import com.example.myapplication.EncryptUtil;
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
//        byte[] data = {0x0c, 0x33, (byte)0x8a, 0x04, 0x0e, (byte)0xa9, 0x37, 0x0d, (byte)0xae, (byte)0xdb,
//                0x20, (byte)0xa0, 0x2d, 0x31, (byte)0xc2, (byte)0xc3, (byte)0xeb, (byte)0xf9, 0x19, 0x50,
//                (byte)0xf9, (byte)0xff, (byte)0xa2, (byte)0xc4, 0x1f, 0x65, (byte)0x80, 0xd, 0x12, 0x5b,
//                0x24, (byte)0xf0, (byte)0xde, (byte)0xbe, 0x1f, 0x2c, 0x1e, 0x16, (byte)0xaf, 0x76,
//                0x0d, 0x48, (byte)0xba, 0x22, (byte)0xb4, (byte)0xf9, (byte)0x8a, (byte)0xb9, (byte)0xc6, 0x6e,
//                0x3b, (byte)0xac, (byte)0xef, (byte)0xf7, (byte)0x96, (byte)0xe8, 0x2d, (byte)0xb8, (byte)0x9d, (byte)0xba,
//                (byte)0x94, 0x58, (byte)0xf0, 0x14, 0x31, (byte)0xaf, (byte)0xd5, 0x0d, 0x3b, 0x4f,
//                (byte)0x97, 0x65, (byte)0xbc, (byte)0xf0, 0x22, (byte)0xf4, (byte)0x83, 0x1b, (byte)0xdf, (byte)0xc1,
//                (byte)0xbf};
//        byte[] result = EncryptUtil.encrypt(600059737, 100000, data);
//        Logger.i(Tools.bytesToHexString(result));
//        byte[] encrypt3 = EncryptUtil.encrypt(600059737, 100000, result);
//        Logger.i(Tools.bytesToHexString(encrypt3));


        byte[] encryptData = EncryptUtil.encrypt(600059737, 100000, new byte[]{0x19, 0x08, 0x31, 0x12});
        String result = Tools.bytesToHexString(encryptData);
        Log.i("rankang", result);
        byte[] encryptData2 = EncryptUtil.encrypt(600059737, 100000, encryptData);
        String result2 = Tools.bytesToHexString(encryptData2);
        Log.i("rankang", result2);
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
        } else if("LogoutFragment".equals(jumpTo)) {
            LogoutFragment logoutFragment = LogoutFragment.newInstance("", "");
            logoutFragment.setCallback(this);
            switchFragment(logoutFragment);
        } else if("StudyDataFragment".equals(jumpTo)) {
            StudyDataFragment studyDataFragment = StudyDataFragment.newInstance("", "");
            studyDataFragment.setCallback(this);
            switchFragment(studyDataFragment);
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
        //t.addToBackStack("StudentLoginFragment");
    }
}
