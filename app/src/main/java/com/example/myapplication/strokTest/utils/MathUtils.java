package com.example.myapplication.strokTest.utils;

import android.graphics.PointF;

import com.example.myapplication.strokTest.TimePoint;

public class MathUtils {
    public static void calAngleBisectorPoints(TimePoint p0, TimePoint p1, TimePoint p2,
                                              PointF pTop, PointF pBottom) {
        float d1 = distance(p0.x, p0.y, p1.x, p1.y);
        float d2 = distance(p2.x, p2.y, p1.x, p1.y);

        float offsetX;
        float offsetY;

        //共线
        if ((p0.x - p1.x) * (p2.y - p1.y) - (p2.x - p1.x) * (p0.y - p1.y) == 0) {
            if (p0.x == p1.x) {
                offsetX = p1.width;
                offsetY = 0;
            } else if (p0.y == p1.y) {
                offsetX = 0;
                offsetY = p1.width;
            } else {
                double k = (p0.x - p1.x) / (p0.y - p1.y);
                k = -1 / k;
                offsetX = calOffsetBySlope(p1.width, k, true);
                offsetY = calOffsetBySlope(p1.width, k, false);
            }
        } else {
            //两个单位向量的向量和为角平分线的向量
            float sx = (p0.x - p1.x) / d1 + (p2.x - p1.x) / d2;
            float sy = (p0.y - p1.y) / d1 + (p2.y - p1.y) / d2;

            float ds = distance(0, 0, sx, sy);

            offsetX = p1.width * sx / ds;
            offsetY = p1.width * sy / ds;
        }


        pTop.set(p1.x + offsetX, p1.y + offsetY);
        pBottom.set(p1.x - offsetX, p1.y - offsetY);
    }

    public static float distance(float x0, float y0, float x1, float y1) {
        return (float) Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
    }

    /**
     * 通过斜率计算点
     * @param point 点
     * @param k 斜率
     * @param flip 是否翻转
     */
    public static void calPointBySlope(TimePoint point, double k, PointF p1,
                                         PointF p2,boolean flip) {
        int f = flip ? -1 : 1;
        float x = calOffsetBySlope(point.width, k, true) * f;
        float y = calOffsetBySlope(point.width, k, false) * f;

        p1.set(point.x + x, point.y + y);
        p2.set(point.x - x, point.y - y);
    }

    public static float calOffsetBySlope(float radius, double k, boolean isX) {
        double d = Math.atan(k);
        if (isX) {
            return (float) (Math.cos(d) * radius);
        } else {
            return (float) (Math.sin(d) * radius);
        }
    }

    /**
     * 判断两点是否在同一侧
     * @return bool
     */
    public static boolean isSameSide(float x0, float y0, float x1, float y1,
                               float x2, float y2, float x3, float y3) {
        if (x0 == x1) {
            return x2 >= x0 && x3 >= x0 || x2 < x0 && x3 < x0;
        } else if (y0 == y1) {
            return y2 >= y0 && y3 >= y0 || y2 < y0 && y3 < y0;
        } else {
            double k = (y0 - y1) / (x0 - x1);
            double b = y0 - k * x0;
            return y2 >= getEquationValue(k, b, x2) && y3 >= getEquationValue(k, b, x3) ||
                    y2 < getEquationValue(k, b, x2) && y3 < getEquationValue(k, b, x3);
        }
    }

    public static double getEquationValue(double k, double b, double x) {
        return k * x + b;
    }

}
