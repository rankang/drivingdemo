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
import com.driving.application.event.EvtBusEntity;
import com.driving.application.jt808.GpsPackage;
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.jt808.frame.StudentLogoutFrame;
import com.driving.application.jt808.frame.TeacherLogoutFrame;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogoutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mStuLoginOut;
    private Button mTeacherLoginOut;
    private TextView mLogMessage;
    private Button mNextStep;
    public LogoutFragment() {
        // Required empty public constructor
    }

    public static LogoutFragment newInstance(String param1, String param2) {
        LogoutFragment fragment = new LogoutFragment();
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
        return inflater.inflate(R.layout.fragment_student_logout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLogMessage = view.findViewById(R.id.log_message);
        mStuLoginOut = view.findViewById(R.id.student_logout);
        mTeacherLoginOut = view.findViewById(R.id.teacher_logout);
        mStuLoginOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                byte[] data = createStuLogoutFrame();
                ConnectManager.getInstance().sendData(data);
            }
        });
        mTeacherLoginOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                byte[] data = createTeacherLogoutData();
                ConnectManager.getInstance().sendData(data);
            }
        });

        mNextStep = view.findViewById(R.id.next_step);
        mNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mCallback) {
                    mCallback.onNext(StudyDataFragment.class.getSimpleName());
                }
            }
        });
    }

    private byte[] createStuLogoutFrame() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, -1);
        Date startDate = c.getTime();

        String time = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(date);
        //24879660.0000,102833220.0000
        // 25010846是纬度  102687371是经度
        GpsPackage gpsPackage = new GpsPackage(time,25010846, 102687371,
                0, 0, 0, 0, 0, 32);
        byte dataType = 0x00;
        byte[] reverse = new byte[18];
        byte grade = 0x22;
        String startTime = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(startDate);
        String endTime = time;
        // 0x00：正常退出
        //0x01：未验证指纹退出
        byte loginOutCode = 0x00;
        int curFinishKm = 100;
        int curStudyHour = 2;
        int maxSpeed = 80;
        int mediaId = 1000;
        JT808ExtFrame jt808ExtFrame = new StudentLogoutFrame(Utils.KEY, Utils.VENDOR_ID, Utils.TERMINAL_PHONE_NUMBER,
                dataType, Utils.studentLoginNumByteArray, Utils.STUDENT_IC, Utils.STUDENT_NUM, reverse,
                startTime, endTime, grade, Utils.TEACHER_NUM, Utils.SCHOOL_NUM, loginOutCode, 0, curFinishKm,
                curStudyHour, maxSpeed, mediaId, gpsPackage);
       return  jt808ExtFrame.getMessage();
    }

    private byte[] createTeacherLogoutData() {
        Date date = new Date();
        String time = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(date);
        GpsPackage gpsPackage = new GpsPackage(time,25010846, 102687371,
                0, 0, 0, 0, 0, 32);
        byte dataType = 0x00;
        byte[] reverse = new byte[18];
        JT808ExtFrame jt808ExtFrame = new TeacherLogoutFrame(Utils.KEY, Utils.VENDOR_ID,
                Utils.TERMINAL_PHONE_NUMBER, dataType, Utils.teacherLoginNumByteArray, Utils.TEACHER_IC,
                Utils.TEACHER_NUM, reverse, Utils.SCHOOL_NUM, gpsPackage);
        return jt808ExtFrame.getMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponse(EvtBusEntity entity) {
        // 01 c5 bf df f3 7a a6 35 ad 5e 98 ae bd
        byte[] data = entity.data;
        int msgId = entity.msgId;
        if(null != data && data.length > 0) {
            if(MSGID.TEACHER_LOGOUT_RESPONSE == msgId) {
                int teacherNum = Tools.byte2Int(new byte[]{data[7], data[8], data[9], data[10]});
                if(data[0] == 0x01) {
                    StringBuffer sb = new StringBuffer(mLogMessage.getText().toString());
                    sb.append("教练登出成功\n")
                            .append("教练编号:")
                            .append(teacherNum).append("\n");
                    mLogMessage.setText(sb.toString());
                } else {
                    StringBuffer sb = new StringBuffer(mLogMessage.getText().toString());
                    sb.append("教练登出失败\n")
                            .append("教练编号:")
                            .append(teacherNum)
                            .append("\n");
                }
            } else if(MSGID.STUDENT_LOGOUT_RESPONSE == msgId) {
                if(data[0] == 0x01) {
                    StringBuffer sb = new StringBuffer("学员登出成功\n");
                    int stuNum = Tools.byte2Int(new byte[]{data[9], data[10], data[11], data[12]});
                    sb.append("学员编号:")
                            .append(stuNum)
                            .append("\n");

                    mLogMessage.setText(sb.toString());
                    mTeacherLoginOut.setEnabled(true);
                } else {

                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    private Callback mCallback = null;
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
