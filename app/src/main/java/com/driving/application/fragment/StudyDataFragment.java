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
import com.driving.application.connect.ConnectManager;
import com.driving.application.event.EvtBusEntity;
import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.frame.StudyDataFrame;
import com.driving.application.util.PrefsUtil;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudyDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button mUploadBtn;
    Button mNextStep;
    public StudyDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudyDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudyDataFragment newInstance(String param1, String param2) {
        StudyDataFragment fragment = new StudyDataFragment();
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
        return inflater.inflate(R.layout.fragment_study_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.upload_study_data).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                byte[] data = getStudyData();
                ConnectManager.getInstance().sendData(data);
            }
        });
        mNextStep = view.findViewById(R.id.next_step);
        mNextStep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(null != mCallback) {
                    mCallback.onNext(LogoutFragment.class.getSimpleName());
                }
            }
        });
    }


    private byte[] getStudyData() {
        // 25010846是纬度  102687371是经度
        Date date = new Date();
        String time = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(date);
        GpsPackage gpsPackage = new GpsPackage(time,25010846, 102687371,
                0, 0, 0, 0, 0, 32);
        byte reportType = 0x01; //自动
        // 构建学时编号
        int studyFlowNum = PrefsUtil.getStudyFlowNum();
        byte[] studyFlowNumByteArray = Tools.intTo2Bytes(studyFlowNum);
        byte[] studyNumByteArray = new byte[10];
        int index = 0;
        for(byte b : Utils.studentLoginNumByteArray) {
            studyNumByteArray[index++] = b;
        }
        for(byte b : studyFlowNumByteArray) {
            studyNumByteArray[index++] = b;
        }
        // 开始时间，即学员登录的时间
        String startRecordTime = Utils.startRecordTime;
        String endRecordTime = time;
        // 学习学时	WROD		单位：分钟
        int studyTime = 60;// 60 分钟
        // 行驶里程	DWORD		单位：米
        int driveKm = 1000;// 1000m
        // 学习科目	BYTE
        byte grade = 3;
        // 学时状态
        byte studyStatus = 0x00;
        JT808ExtFrame jt808ExtFrame = new StudyDataFrame(Utils.KEY, Utils.VENDOR_ID, Utils.TERMINAL_PHONE_NUMBER,
                reportType, studyNumByteArray, startRecordTime, endRecordTime, studyTime, driveKm,
                Utils.STUDENT_NUM, Utils.TEACHER_NUM, grade, studyStatus, Utils.STUDENT_IC, Utils.TEACHER_IC, gpsPackage);
        return jt808ExtFrame.getMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponse(EvtBusEntity entity) {

    }

    private Callback mCallback = null;
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
