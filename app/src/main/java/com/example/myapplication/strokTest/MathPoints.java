package com.example.myapplication.strokTest;

import android.graphics.PointF;

/**
 * 保存运算中需要的点，避免重复新建对象。
 */
public class MathPoints {
    PointF pA = new PointF(),
            pB = new PointF(),
            pC = new PointF(),
            pD = new PointF(),
            pMT = new PointF(),
            pMB = new PointF();

    PointF pTop = new PointF(),
            pBottom = new PointF();
}
