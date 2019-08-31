package com.driving.application.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.driving.application.jt808.JT808ExtFrame;
import com.driving.application.jt808.JT808StFrame;
import com.driving.application.jt808.MSGID;
import com.driving.application.jt808.frame.MediaEvtFrame;
import com.driving.application.jt808.frame.PictureUploadFrame;
import com.driving.application.util.Logger;
import com.driving.application.util.Tools;
import com.driving.application.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadPicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadPicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView mLogMessage;
    private Button mNextStep;
    public UploadPicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadPicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadPicFragment newInstance(String param1, String param2) {
        UploadPicFragment fragment = new UploadPicFragment();
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
        return inflater.inflate(R.layout.fragment_upload_pic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLogMessage = view.findViewById(R.id.log_message);
        mNextStep = view.findViewById(R.id.next_step);
        view.findViewById(R.id.upload_pic).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mLogMessage.setText("");
                byte[] data = createMediaEvtFrame();
                ConnectManager.getInstance().sendData(data);
            }
        });
        mNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onNext(LogoutFragment.class.getSimpleName());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    /**
     * create frame data
     * @return
     */
    private void sendUpLoadFrameData() {
        // 读取文件
        new UploadPictureTask().execute();
    }
    private byte[] createMediaEvtFrame() {
        int mediaId = 1000;
        byte mediaType = 0;
        byte mediaEncode = 0;
        // 6：学员签到；
        byte evtCode = 0x06;
        byte chanelId = 1;
        JT808StFrame jt808StFrame = new MediaEvtFrame(Utils.TERMINAL_PHONE_NUMBER,
                mediaId, mediaType, mediaEncode, evtCode, chanelId, Utils.TEACHER_NUM, Utils.STUDENT_NUM,
                Utils.SCHOOL_NUM, Utils.studentLoginNumByteArray, 0);
        return jt808StFrame.getMessage();
    }




    public static class UploadPictureTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                int packageSize = 512;
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"test_upload_small.jpg";
                Logger.i("----------------------------------"+path);
                File file = new File(path);
                FileInputStream inStream = new FileInputStream(file);
                byte[] buffer = new byte[packageSize];
                // 上传数据通道ID
                byte chancelId = 1;
                float fileSize = (float)file.length();
                // 总包数
                byte totalPackageCount = (byte)Math.ceil(fileSize/packageSize);
                int len;
                // 多媒体ID
                int picId = 1000;
                byte cPackageNum = 1;
                while ((len = inStream.read(buffer)) != -1) {
                    byte[] curPackageData = new byte[len];
                    System.arraycopy(buffer, 0, curPackageData, 0, len);
                    // 构建通信帧
                    JT808StFrame jt808ExtFrame
                            = new PictureUploadFrame(Utils.TERMINAL_PHONE_NUMBER,
                            picId, totalPackageCount, cPackageNum,  chancelId, buffer);
                    byte[] data = jt808ExtFrame.getMessage();
                    ConnectManager.getInstance().sendData(data);
                    cPackageNum++;
                }
                Logger.i("------------------------picture upload finish-----------------------------");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }
    }
    int count = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponse(EvtBusEntity entity) {
        byte[] data = entity.data;
        int msgId = entity.msgId;
        if(null != data && data.length > 0) {
            // 平台通用回复
            if(msgId == MSGID.COMMON_RES && data[4] == 0x00 ) {
                int requestId = Tools.twoBytes2Int(new byte[]{data[2], data[3]});
                if(MSGID.PICTURE_EVT_REQUEST == requestId) {
                    //int flowNum = Tools.twoBytes2Int(new byte[]{data[0], data[1]});
                    StringBuffer sb = new StringBuffer("多媒体事件信息上传成功\n");
                    sb.append("消息流水:").append(Tools.bytesToHexString(new byte[]{data[0], data[1]})).append("\n")
                            .append("开始上传多媒体数据").append("\n");
                    mLogMessage.setText(sb.toString());
                    sendUpLoadFrameData();
                } else if(MSGID.PICTURE_UPLOAD_REQUEST == requestId) {
                    count++;
                    StringBuffer sb = new StringBuffer(mLogMessage.getText().toString());
                    sb.append("第"+count+"帧发送成功").append("\n");
                    mLogMessage.setText(sb.toString());
                }
            } else if(MSGID.PICTURE_UPLOAD_LAST_PACKAGE_RESPONSE == msgId) {
                StringBuffer sb = new StringBuffer(mLogMessage.getText().toString());
                String mediaId = Tools.bytesToHexString(new byte[]{data[0], data[1], data[2], data[3]});
                int resendPackageCount = data[4] & 0xff;
                sb.append("多媒体数据已经发送完成").append("\n")
                        .append("多媒体ID:").append(mediaId).append("\n")
                        .append("重传包数:").append(resendPackageCount).append("\n");
                if(resendPackageCount > 0) {
                    String resendPackageSequence = "重传包序号:";
                    for(int i=5; i < data.length; i++) {
                        resendPackageSequence += (data[i]+", ");
                    }
                    sb.append(resendPackageSequence);
                } else {
                    mNextStep.setEnabled(true);
                }
                mLogMessage.setText(sb.toString());
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
