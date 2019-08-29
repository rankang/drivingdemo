package com.driving.application.jt808;

public interface MSGID {
    /**注册*/
    int REGISTER_REQ = 0X0100;
    int REGISTER_RES = 0x8100;

    /**鉴权*/
    int VALIDATE_REQ = 0x0102;

    int COMMON_RES = 0X8001;

    /**教练登录*/
    int TEACHER_LOGIN = 0x0101;
    /**教练登出*/
    int TEACHER_LOGOUT = 0x0102;
    /**学生登录*/
    int STUDENT_LOGIN = 0x0201;
    /**学生登出*/
    int STUDENT_LOGOUT = 0x0202;


    // 心跳
    int HEART_BEAT = 0X0002;

    int PIC_UPLOAD = 0x1801;
}
