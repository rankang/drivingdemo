package com.driving.application;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.driving.application.fragment.StudentLoginFragment;
import com.driving.application.fragment.TeacherLoginFragment;
import com.driving.application.fragment.UploadPicFragment;


public class MainActivity extends AppCompatActivity implements Callback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpFragment();
    }

    private void setUpFragment() {
        TeacherLoginFragment tlf = TeacherLoginFragment.newInstance("", "");
        tlf.setCallback(this);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.fragment_container, tlf);
        t.commit();
    }


    @Override
    public void onNext(String jumpTo) {
        if("StudentLoginFragment".equals(jumpTo)) {
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
