package com.example.myapplication.pen;

public interface IPenConfig {

    //这个控制笔锋的控制值
     float DIS_VEL_CAL_FACTOR = 0.02f;

    //绘制计算的次数，数值越小计算的次数越多，需要折中
     int STEPFACTOR = 10;
}
