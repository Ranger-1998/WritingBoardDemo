package com.example.myapplication.strokTest;

import android.view.MotionEvent;

public class TestClass {
//   // 使用余切函数[mw_shl_code=java,true]
//     public boolean dispatchTouchEvent(MotionEvent ev) {
//     //x2,y2是当前点,x1,y1是上一个点
//     if(ev.getAction() == MotionEvent.ACTION_MOVE) {
//     if(x1 < 0) {
//     x1 = ev.getX();
//     y1 = ev.getY();
//     } else {
//     double x2 = ev.getX();
//     double y2 = ev.getY();
//     if(angle(x1 , y1) - angle(x2 , y2) > Math.PI || angle(x2 , y2) > angle(x1 , y1)) {
//     //顺时针（前一种是轮过一圈的情况） } else { //逆时针
//     }
//     x1 = x2; y1 = y2;
//     } System.out.println(x1 + "," + y1);
//     } return super.dispatchTouchEvent(ev);
//     }
//     //x0, y0是圆心
//     public static double angle(double x1, double y1) {
//     return Math.atan2(x1-x0, y0-y1);
//     }
}
