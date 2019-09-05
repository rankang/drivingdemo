package com.driving.application.jt808;

public interface MSGID {
    /**注册*/
    int REGISTER_REQ = 0X0100;
    int REGISTER_RES = 0x8100;

    /**鉴权*/
    int VALIDATE_REQ = 0x0102;
    int COMMON_RES = 0X8001;

    /**教练登录*/
    int TEACHER_LOGIN_REQ = 0x0101;
    int TEACHER_LOGIN_RES_REAL = 0X8101;
    int TEACHER_LOGIN_RES_HISTORY = 0X8501;
    /**教练登出*/
    int TEACHER_LOGOUT = 0x0102;
    /**学生登录*/
    int STUDENT_LOGIN_REQ = 0x0201;
    // 实时数据
    int STUDENT_LOGIN_RES_REAL = 0X8201;
    // 补传数据
    int HISTORY_DATA_RESPONSE = 0x8501;
    /**学生登出*/
    int STUDENT_LOGOUT = 0x0202;


    // 心跳
    int HEART_BEAT = 0X0002;


    // 上行透传
    int TRANS_REQ = 0X0900;
    // 下行透传
    int TRANS_RES = 0X8900;
    // 图片
    // 图片事件上传
    int PICTURE_RESPONSE = 0x8001;
    int PICTURE_EVT_REQUEST = 0x0800;
    int PICTURE_UPLOAD_REQUEST = 0x1801;
    // 图片上传
    int PICTURE_UPLOAD_LAST_PACKAGE_RESPONSE = 0x8800;

    // 教练登出
    int TEACHER_LOGOUT_REQUEST = 0x0102;
    int TEACHER_LOGOUT_RESPONSE = 0x8102;
    // 学员登出
    int STUDENT_LOGOUT_REQUEST = 0x0202;
    int STUDENT_LOGOUT_RESPONSE = 0x8202;

    // 学时数据上传
    int UPLOAD_STUDY_DATA = 0x0301;
}
