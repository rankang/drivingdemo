package com.driving.application.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.driving.application.Callback;
import com.driving.application.R;
import com.driving.application.connect.ConnectManager;
import com.driving.application.jt808.BaseFrame;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.util.Logger;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherLoginFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView mLogMessage;
    private Button mNextStep;

    public TeacherLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherLoginFragment.
     */
    public static TeacherLoginFragment newInstance(String param1, String param2) {
        TeacherLoginFragment fragment = new TeacherLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button teacherLogin = view.findViewById(R.id.teacher_login);
        Logger.i(Tools.bytesToHexString("云A5300学".getBytes(Charset.forName("gbk"))));
        //String car = new String(new byte[]{(byte)0xd4, (byte)0xc6, (byte)0x41, 0x35, 0x33, 0x30, 0x30, (byte)0xd1, (byte)0xA7}, Charset.forName("gbk"));
        //46 00 36 30 30 30 35 39 37 33 37
        //String car2 = new String(new byte[]{(byte)0xd4, (byte)0xc6, (byte)0x41, 0x35, 0x33, 0x30, 0x30, (byte)0xd1, (byte)0xA7}, Charset.forName("gbk"));
        //Logger.i(car);

        teacherLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = createRequest();
                // sender
                ConnectManager.getInstance().sendData(data);
            }
        });

        mNextStep = view.findViewById(R.id.next_step);
        mNextStep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onNext(StudentLoginFragment.class.getSimpleName());
                }
            }
        });

        mLogMessage = view.findViewById(R.id.log_message);
    }


    private byte[] createRequest() {
        JT808ExtFrame f = new JT808ExtFrame();
        //// 模拟数据
        // 教练：63071952   姚志明    身份证530127198509281710
        //驾校编号：53010098
        byte[] bodyPrefix = new byte[51];
        int index = 0;
        //0：实时数据
        //1：补传数据
        // 由于只考虑在线情况所以此处赋值为0
        bodyPrefix[index++] = 0x00;
        // 教练登录编号6给字节 BCD码+流水号
        String dateTime = new SimpleDateFormat("yyyyMMddHH", Locale.CHINESE).format(new Date());
        // 2019年08 月22 日22时
        byte[] bcdDateTime = Tools.getBCDByteArray(dateTime);//new byte[]{0x19, 0x08, 0x22, 0x22};//Tools.getBCDByteArray(dateTime);
        for(int i=0; i<bcdDateTime.length; i++) {
            bodyPrefix[index++] = bcdDateTime[i];
        }

        int flowNum = BaseFrame.getFlowNum();
        byte[] flowNumBytes = Tools.intTo2Bytes(flowNum);
        bodyPrefix[index++] = flowNumBytes[0];
        bodyPrefix[index++] = flowNumBytes[1];


        // 教练IC
        byte[] originIcData = Utils.icCard.getBytes(Charset.forName("gbk"));
        Logger.i("---------------originIcData----"+Tools.bytesToHexString(originIcData));
        byte[] icBytes = new byte[18];
        for(int i=0; i<originIcData.length; i++) {
            icBytes[i] = originIcData[i];
        }
        for(byte b : icBytes) {
            bodyPrefix[index++] = b;
        }

        // 教练编号
        byte[] teachNumBytes = Tools.intTo4Bytes(Utils.teacherNum);
        for (byte item : teachNumBytes) {
            bodyPrefix[index++] = item;
        }
        // 消息项reverse 18个字节
        index += 18;
        // 教练驾校编号
        byte[] schoolBytes = Tools.intTo4Bytes(Utils.schoolNum);
        for(byte b : schoolBytes) {
            bodyPrefix[index++] = b;
        }

        byte[] gpsPackage = f.createGpsPackage(1000, 1000,
                10, 50, 49, 10, 1, 1);
        Logger.i("---------------teacher login bodyPrefix----------"+Tools.bytesToHexString(bodyPrefix));
        byte[] msg_body = f.createMsgBody(bodyPrefix, gpsPackage);
        int key = Utils.key;
        byte[] keyBytes = Tools.intTo4Bytes(key);
        byte[] header = f.createMsgHeader(MSGID.TEACHER_LOGIN, flowNum, 1000, keyBytes);
        Logger.i("---------------teacher login header----------"+Tools.bytesToHexString(header));
        byte[] data = f.getFrameData(key, header, msg_body);
        return data;
    }

    private Callback mCallback = null;
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
