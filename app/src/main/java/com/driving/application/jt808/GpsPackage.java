package com.driving.application.jt808;

/**
 * GPS 数据包
 *  总共30个字节
 */
public class GpsPackage {
    // 时间 年月日时分秒 BCD 6byte
    public String time;

    // 纬度 X度* 1000000 4byte
    public int lat;

    // 经度 Y度* 1000000 4byte
    public int lng;

    // 高度 单位：米 2byte
    public int height;

    // 速度 单位：1/10km/h 2byte
    public int speed;

    // 记录仪速度 单位：1/10km/h 2byte
    public int recordDevSpeed;

    // 方向 单位：0-359度,正北0,顺时针 2byte
    public int direction;

    // 状态1 JT808状态定义 4byte
    public int status1;

    // 状态2 4byte
    public int status2;

    public GpsPackage(String time, int lat, int lng, int height, int speed, int recordDevSpeed, int direction, int status1, int status2) {
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.height = height;
        this.speed = speed;
        this.recordDevSpeed = recordDevSpeed;
        this.direction = direction;
        this.status1 = status1;
        this.status2 = status2;
    }
}
