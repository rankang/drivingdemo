package com.driving.application.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.driving.application.R;
import com.driving.application.connect.ConnectManager;
import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.MSGID;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.Charset;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        return inflater.inflate(R.layout.fragment_register, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button registerBtn = view.findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                byte[] requestData = createRequestData();
                ConnectManager.getInstance().sendData(requestData);
                // 00
                // 01 00 36 30 30 30 35 39 37 33 37
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisted(byte[] data) {
        // TODO  处理数据
    }

    private byte[] createRequestData() {
        //  最好写成数据类来构建
        // 构建注册body 数据
        JT808StFrame stFrame = new JT808StFrame();
        //  18469127302 云A5300学
        String carNumber = "云A5300学";
        byte[] carNumberBytes = carNumber.getBytes(Charset.forName("gbk"));
        byte[] body = new byte[25+carNumberBytes.length];
        int index = 0;
        // 530101 000000 取前两位和后4位-> 00 35 00 00
        // 省域ID
        byte[] provinceBytes = {0x00, 0x35};
        body[index++] = provinceBytes[0];
        body[index++] = provinceBytes[1];
        //  市县域ID
        byte[] cityBytes = {0x00, 0x00};
        body[index++] = cityBytes[0];
        body[index++] = cityBytes[1];
        // 制造商ID 自定义
        byte[] vendorIdBytes = {0x53, 0x31, 0x30, 0x30,  0x30}; // 制造商ID =5
        for(byte b : vendorIdBytes) {
            body[index++] = b;
        }
        // 终端型号， 自定义 8个字节
        byte[] terminalModelBytes = {0x53, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
        for(byte b : terminalModelBytes) {
            body[index++] = b;
        }
        // 终端ID 自定义7个字节
        byte[] terminalIdBytes = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};
        for(byte b : terminalIdBytes) {
            body[index++] = b;
        }
        // 车牌颜色 默认
        body[index++] = 0x02; // 车牌颜色
        // 车牌 需要和手机号对应
        for(byte b : carNumberBytes) {
            body[index++] = b;
        }

        byte[] header = stFrame.createMsgHeader(MSGID.REGISTER, "18469127302", body.length);
        byte[] msgData = stFrame.createMsgData(header, body);
        return msgData;
    }


}
