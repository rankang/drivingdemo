package com.driving.application.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.driving.application.Callback;
import com.driving.application.R;
import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.frame.StudentLoginFrame;
import com.driving.application.util.PrefsUtil;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentLoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public StudentLoginFragment() {
        // Required empty public constructor
    }

    public static StudentLoginFragment newInstance(String param1, String param2) {
        StudentLoginFragment fragment = new StudentLoginFragment();
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
        return inflater.inflate(R.layout.fragment_student_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button stuLogin = view.findViewById(R.id.student_login);
        stuLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        Button nextStep = view.findViewById(R.id.next_step);
        nextStep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onNext("PicUploadFragment");
                }
            }
        });
    }


    private byte[] createStuFrameData() {
        // 25010846是纬度  102687371是经度
        Date date = new Date();
        String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).format(date);
        GpsPackage gpsPackage = new GpsPackage(time,25010846, 102687371,
                0, 0, 0, 0, 0, 32);

        String hourTime = new SimpleDateFormat("yyyyMMddHH", Locale.CHINESE).format(date);

        // 实时数据
        byte dataType = 0x00;

        // 保留18字节
        byte[] reverse = new byte[18];
        String terminalPhoneNumber = Utils.TERMINAL_PHONE_NUMBER;
        // 学员登录编号 = 教练登录编号 + 学员登录流水号 共8字节
        byte[] studentLoginNum = new byte[8];
        byte[] stuFlowNumBytes = Tools.intTo2Bytes(PrefsUtil.getStudentLoginFlowNum());

       // JT808ExtFrame jt808ExtFrame = new StudentLoginFrame(Utils.KEY, dataType, Utils.VENDOR_ID, terminalPhoneNumber, studentLoginNum)
        return null;
    }

    private Callback mCallback = null;
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
