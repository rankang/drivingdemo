package com.driving.application;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.driving.application.fragment.RegisterFragment;
import com.driving.application.fragment.StudentLoginFragment;
import com.driving.application.fragment.TeacherLoginFragment;
import com.driving.application.fragment.UploadPicFragment;
import com.driving.application.util.Logger;

import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity implements Callback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpFragment();
        //600059737
        Logger.i(new String(new byte[]{0x36, 0x30, 0x30, 0x30, 0x35, 0x39, 0x37, 0x33, 0x37}));
    }

    private void setUpFragment() {
        RegisterFragment tlf = RegisterFragment.newInstance("", "");
        tlf.setCallback(this);
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


    private void switchFragment(Fragment fragment) {

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.fragment_container, fragment);
        t.commit();
        t.addToBackStack("StudentLoginFragment");
    }
}
