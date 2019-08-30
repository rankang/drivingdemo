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
import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.jt808.frame.TeacherLoginFrame;
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
        //Logger.i(Tools.bytesToHexString("云A5300学".getBytes(Charset.forName("gbk"))));
        //String car = new String(new byte[]{(byte)0xd4, (byte)0xc6, (byte)0x41, 0x35, 0x33, 0x30, 0x30, (byte)0xd1, (byte)0xA7}, Charset.forName("gbk"));
        //46 00 36 30 30 30 35 39 37 33 37
        //String car2 = new String(new byte[]{(byte)0xd4, (byte)0xc6, (byte)0x41, 0x35, 0x33, 0x30, 0x30, (byte)0xd1, (byte)0xA7}, Charset.forName("gbk"));
        //Logger.i(car);

        teacherLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = createTeacherLoginRequest();
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


    private byte[] createTeacherLoginRequest() {
        Date date = new Date();
        String time = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(date);
        //24879660.0000,102833220.0000
        // 25010846是纬度  102687371是经度
        GpsPackage gpsPackage = new GpsPackage(time,25010846, 102687371,
                0, 0, 0, 0, 0, 32);

        String hourTime = new SimpleDateFormat("yyMMddHH", Locale.CHINESE).format(date);

        // 实时数据
        byte dataType = 0x00;

        // 保留18字节
        byte[] reverse = new byte[18];

        // 终端手机号
        String terminalPhoneNumber = Utils.TERMINAL_PHONE_NUMBER;
        JT808ExtFrame tlf = new TeacherLoginFrame(Utils.KEY, Utils.VENDOR_ID, terminalPhoneNumber,
                dataType, hourTime, Utils.TEACHER_IC, Utils.TEACHER_NUM, reverse, Utils.SCHOOL_NUM, gpsPackage);
        return tlf.getMessage();
    }

    private Callback mCallback = null;
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
