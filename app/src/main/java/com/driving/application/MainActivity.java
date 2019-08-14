package com.driving.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.driving.application.connect.ConnectManager;
import com.driving.application.jt808.Frame;
import com.driving.application.util.Logger;
import com.driving.application.util.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    int flowNum = 1;
    int teacherNum = 100;
    int schoolNum = 100;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.teacher_sign:
                Frame f = new Frame();
                byte[] bodyBaseData = new byte[51];
                int index = 0;
                /**
                 * 0：实时数据
                 * 1：补传数据
                 */
                bodyBaseData[index++] = 0x00;
                // 教练登录编号6给字节 BCD码+流水号
                String dateTime = new SimpleDateFormat("yyyyMMddHH", Locale.CHINESE).format(new Date());
                byte[] bcdDateTime = Tools.getBCDByteArray(dateTime);
                for(int i=0; i<bcdDateTime.length; i++) {
                    bodyBaseData[index++] = bcdDateTime[i];
                }
                // 流水号1-65535
                if(flowNum > 65535) {
                    flowNum = 1;
                }
                byte[] flowNumBytes = Tools.intTo2Bytes(flowNum);
                bodyBaseData[index++] = flowNumBytes[0];
                bodyBaseData[index++] = flowNumBytes[1];
                flowNum++;

                // 教练IC
                byte[] originIcData = "AY010101".getBytes();
                byte[] icBytes = new byte[18];
                for(int i=0; i<originIcData.length; i++) {
                    icBytes[i] = originIcData[i];
                }
                for(byte b : icBytes) {
                    bodyBaseData[index++] = b;
                }

                // 教练编号
                byte[] teachNumBytes = Tools.intTo4Bytes(teacherNum);
                for (byte item : teachNumBytes) {
                    bodyBaseData[index++] = item;
                }
                // reverse 18个字节
                index += 18;
                // 教练驾校编号
                byte[] schoolBytes = Tools.intTo4Bytes(schoolNum);
                for(byte b : schoolBytes) {
                    bodyBaseData[index++] = b;
                }

                byte[] gpsPackage = f.createGpsPackage(1000, 1000, 10, 50, 49, 10, 1, 1);
                byte[] body = f.createMsgBody(bodyBaseData, gpsPackage);
                byte[] header = f.createMsgHeader(0x0101, flowNum, 10, new byte[]{0x00, 0x00, 0x00, 0x00});
                byte[] data = f.getFrameData(header, body);

                ConnectManager.getInstance().sendData(data);
                break;
            case R.id.student_sign:
                break;
        }
    }


}
