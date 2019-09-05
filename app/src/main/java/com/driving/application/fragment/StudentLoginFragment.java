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
import com.driving.application.jt808.frame.StudentLoginFrame;
import com.driving.application.util.Logger;
import com.driving.application.util.PrefsUtil;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentLoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mNextStep;
    private TextView mLogMessage;

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
                byte[] data = createStuFrameData();
                ConnectManager.getInstance().sendData(data);
            }
        });
        // 下一步
        mNextStep = view.findViewById(R.id.next_step);
        mNextStep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onNext(UploadPicFragment.class.getSimpleName());
                }
            }
        });
        // 日志信息
        mLogMessage = view.findViewById(R.id.log_message);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    private byte[] createStuFrameData() {
        // 25010846是纬度  102687371是经度
        Date date = new Date();
        String time = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(date);
        GpsPackage gpsPackage = new GpsPackage(time,25010846, 102687371,
                0, 0, 0, 0, 0, 32);
        Utils.startRecordTime = time;
        // 实时数据
        byte dataType = 0x00;
        // 保留18字节
        byte[] reverse = new byte[18];
        // 学员登录编号 = 教练登录编号 + 学员登录流水号 共8字节
        byte[] studentLoginNum = new byte[8];
        int stuLoginFlowNum = PrefsUtil.getStudentLoginFlowNum();
        byte[] stuFlowNumByteArray = Tools.intTo2Bytes(stuLoginFlowNum);

        int index = 0;
        for(byte b : Utils.teacherLoginNumByteArray) {
            studentLoginNum[index++] = b;
        }
        for(byte b : stuFlowNumByteArray) {
            studentLoginNum[index++] = b;
        }
        Utils.studentLoginNumByteArray = studentLoginNum;
        int picId = 1000;
        byte grade = 3;
       JT808ExtFrame jt808ExtFrame = new StudentLoginFrame(Utils.KEY, dataType, Utils.VENDOR_ID,
               Utils.TERMINAL_PHONE_NUMBER, studentLoginNum, Utils.STUDENT_IC, Utils.STUDENT_NUM,
               reverse, grade, Utils.TEACHER_NUM, Utils.SCHOOL_NUM, picId, gpsPackage);
        return jt808ExtFrame.getMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponse(EvtBusEntity entity) {
        if(entity.msgId == MSGID.STUDENT_LOGIN_RES_REAL) {
            // 正常情况数据长度为19
            byte[] data = entity.data;
            if(data != null && data.length > 0) {
                Logger.i("学员登录返回数据=="+Tools.bytesToHexString(data));
                // 成功
                if(data[0] == 0x01) {
                    StringBuffer sb = new StringBuffer("学员登录成功\n");
                    // 1-8 学员登录编号
                    int startPosition = 1;
                    byte[] studentLoginByteArray = new byte[8];
                    // 学员登录编号
                    System.arraycopy(data, startPosition, studentLoginByteArray, 0, studentLoginByteArray.length);
                    String studentLoginNum = Tools.bytesToHexString(studentLoginByteArray).replace(" ", "");
                    startPosition += studentLoginByteArray.length;
                    // 学员编号
                    byte[] studentNumByteArray = new byte[4];
                    System.arraycopy(data, startPosition, studentNumByteArray, 0, studentNumByteArray.length);
                    int studentNum = Tools.byte2Int(studentNumByteArray);
                    startPosition += studentNumByteArray.length;
                    // 科目
                    int grade = data[startPosition];
                    startPosition += 1;

                    byte[] totalStudyHourByteArray = new byte[2];
                    System.arraycopy(data, startPosition, totalStudyHourByteArray, 0, totalStudyHourByteArray.length);
                    int totalStudyHour = Tools.twoBytes2Int(totalStudyHourByteArray);
                    startPosition += totalStudyHourByteArray.length;

                    byte[] finishStudyHourByteArray = new byte[2];
                    System.arraycopy(data, startPosition, finishStudyHourByteArray, 0, finishStudyHourByteArray.length);
                    int finishStudyHour = Tools.twoBytes2Int(finishStudyHourByteArray);
                    // 组装显示数据
                    sb.append("学员登录编号：").append(studentLoginNum).append("\n")
                            .append("学员编号：").append(studentNum).append("\n")
                            .append("科目：").append(grade).append("\n")
                            .append("总学时：").append(totalStudyHour).append("\n")
                            .append("已完成学时：").append(finishStudyHour);
                    mNextStep.setEnabled(true);
                    mLogMessage.setText(sb.toString());
                } else {
                    Logger.i("student login failed");
                }
            } else {
                throw new RuntimeException("学员登录返回数据错误");
            }
        }
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
