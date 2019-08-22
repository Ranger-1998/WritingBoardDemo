package com.example.myapplication;

import android.graphics.PointF;

import com.example.myapplication.strokTest.DrawCurve;
import com.example.myapplication.strokTest.TimePoint;
import com.example.myapplication.strokTest.utils.MathUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public class Node{
        int val;
        int index;

        Node(int val, int index) {
            this.val = val;
            this. index = index;
        }
    }

    @Test
    public void addition_isCorrect() {
        DrawCurve curve = new DrawCurve();
        TimePoint p1 = new TimePoint(100, 100, 1);
        TimePoint p2 = new TimePoint(123, 451, 1);
        TimePoint p3 = new TimePoint(313, 121, 1);
        double d1 = curve.getDegree(p2, p1);
        double d2 = curve.getDegree(p2, p3);
        double midDegree = (d1 + d2) / 2;
        //long t1 = System.currentTimeMillis();

        PointF pt = new PointF();
        PointF pb = new PointF();

        MathUtils.calAngleBisectorPoints(p1, p2, p3, pt, pb);

        System.out.println(pt.x + " " +pb.x);

        pt = curve.calPointBySlope(p2, Math.tan(midDegree), false);
        pb = curve.calPointBySlope(p2, Math.tan(midDegree), true);

        System.out.println(pt.x +"  " +  pb.x);

        //System.out.println(System.currentTimeMillis() - t1);
    }
}