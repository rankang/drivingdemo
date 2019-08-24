package com.driving.application.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUtil {

    private static PrefsUtil mInstance;
    private  Context mContext;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public static void create(Context context) {
        mInstance = new PrefsUtil();
        mInstance.mContext = context;
        mInstance.mContext = context;
        mInstance.sp = mInstance.mContext.getSharedPreferences("drivingdemo", Context.MODE_PRIVATE);
        mInstance.editor = mInstance.sp.edit();
    }

    public static void updateTeachLoginFlowNum() {
        int num = getTeachLoginFlowNum()+1;
        mInstance.editor.putInt("teach_login_flow", num).apply();
    }

    public  static int getTeachLoginFlowNum() {
        int flow = mInstance.sp.getInt("teach_login_flow", 1);
        if(flow > 65535) {
            flow = 1;
        }
        return flow;
    }



}
