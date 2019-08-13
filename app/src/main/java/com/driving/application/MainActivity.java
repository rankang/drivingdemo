package com.driving.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();
    }

    private void setUpView() {
        Button teacherSignBtn = findViewById(R.id.teacher_sign);
        Button studentSignBtn = findViewById(R.id.student_sign);
        teacherSignBtn.setOnClickListener(this);
        studentSignBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.teacher_sign:
                break;
            case R.id.student_sign:
                break;
        }
    }


}
