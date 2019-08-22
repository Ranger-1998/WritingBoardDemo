package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.myapplication.pen.SteelPen;
import com.example.myapplication.strokTest.DrawCurve;
import com.example.myapplication.strokTest.TimePoint;
import com.example.myapplication.strokTest.utils.MathUtils;

import java.util.ArrayList;


public class PenView extends View {

    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;
    private SteelPen pen;
    private float lx, ly;
    private DrawCurve drawCurve;

    public PenView(Context context) {
        this(context, null);
    }

    public PenView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        pen = new SteelPen();
        pen.setPaint(paint);
        drawCurve = new DrawCurve();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        drawRect(canvas, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lx = event.getX();
                ly = event.getY();
                drawCurve.init();

        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            drawCurve.onMove(event, canvas, paint);
        } else {
            drawCurve.init();
        }
        invalidate();

        return true;
    }

    private void drawRect(Canvas canvas, Paint paint) {
        ArrayList<TimePoint> points = new ArrayList<>();
        points.add(new TimePoint(400 ,200, 1));
        points.add(new TimePoint(300, 300, 1));
        points.add(new TimePoint(200, 400, 1));

        PointF pA, pB, pC, pD, pMT = new PointF(), pMB = new PointF();
        float k;
        k = (points.get(1).y - points.get(2).y) / (points.get(1).x - points.get(2).x);
        k = -1 / k;
        pA = calPointByDegree(points.get(1), k, false);
        pB = calPointByDegree(points.get(1), k, true);

        pC = calPointByDegree(points.get(2), k, false);
        pD = calPointByDegree(points.get(2), k, true);

        PointF midTopPoint = new PointF(), midBottomPoint = new PointF();
        midBottomPoint.set((pB.x + pD.x) / 2, (pB.y + pD.y) / 2);
        midTopPoint.set((pA.x + pC.x) / 2, (pA.y + pC.y) / 2);

        MathUtils.calAngleBisectorPoints(points.get(0), points.get(1), points.get(2), pMT, pMB);

        canvas.drawLine(points.get(0).x, points.get(0).y,points.get(1).x,points.get(1).y, paint);
        canvas.drawLine(points.get(1).x, points.get(1).y,points.get(2).x,points.get(2).y, paint);
        paint.setColor(Color.RED);
        canvas.drawCircle(points.get(1).x, points.get(1).y, points.get(1).width, paint);
        paint.setColor(Color.GREEN);
        canvas.drawLine(pMT.x, pMT.y, pMB.x, pMB.y, paint);
        canvas.drawText("PMT", pMT.x, pMT.y, paint);

        for (int i = 0; i < points.size(); i++) {
            canvas.drawText(i +"", points.get(i).x + 10, points.get(i).y + 10, paint);
        }

//        ArrayList<PointF> testD = new ArrayList<>();
//        testD.add(new PointF(300, 200));
//        testD.add(new PointF(400, 200));
//        testD.add(new PointF(400, 300));
//        testD.add(new PointF(400, 400));
//        testD.add(new PointF(300, 400));
//        testD.add(new PointF(200, 400));
//        testD.add(new PointF(200, 300));
//        testD.add(new PointF(200, 200));
//
//        for (PointF p : testD) {
//            canvas.drawLine(300, 300, p.x, p.y, paint);
//            double kk = (300 - p.y) / (300 - p.x);
//            paint.setTextSize(20);
//
//            canvas.drawText(Math.toDegrees(Math.atan2(p.y - 300, p.x - 300)) + "",
//                    p.x + 20, p.y + 20, paint);
//
//            if (testD.indexOf(p) > 3) {
//                double td = getDegreeByK(kk) + getDegreeOffset(new PointF(300, 300), p);
//                PointF pMT = calPointByDegree(new TimePoint(300, 300, 1), kk, false);
//                PointF pMB = calPointByDegree(new TimePoint(300, 300, 1), kk, true);
//
//                canvas.drawText("PMT", pMT.x, pMT.y, paint);
//                canvas.drawText("PMB", pMB.x, pMB.y, paint);
//                canvas.drawLine(pMT.x, pMT.y, pMB.x, pMB.y, paint);
//            }
//
//        }
    }

    private PointF calPointByDegree(TimePoint point, double k, boolean offset) {
        double degree = Math.atan(k);
        degree = offset ? degree + Math.PI : degree;
        float x = (float) (point.x + point.width * Math.cos(degree));
        float y = (float) (point.y + point.width * Math.sin(degree));
        return new PointF(x, y);
    }

    private double getMidDegree(double k1, double k2) {
        return (Math.atan(k1) + Math.atan(k2)) / 2;
    }

    private double getDegreeByK(double k) {
        Log.d("测试角度", "斜率: " + k + ", 角度: " + Math.atan(k) * 180 / Math.PI);
        return Math.atan(k);
    }

    private double getDegreeOffset(PointF p0, PointF p1) {
        if (p1.x > p0.x) {
            if (p1.y >= p0.y) {
                return 0;
            } else {
                return 2 * Math.PI;
            }
        } else {
            return Math.PI;
        }
    }


    private double getDegree(TimePoint p0, TimePoint p1) {
        double offset = getDegreeOffset(p0, p1);
        double k = (p0.y - p1.y) / (p0.x - p1.x);
        return Math.atan(k) + offset;
    }

    private double getDegreeOffset(TimePoint p0, TimePoint p1) {
        if (p1.x > p0.x) {
            if (p1.y >= p0.y) {
                return 0;
            } else {
                return 2 * Math.PI;
            }
        } else {
            return Math.PI;
        }
    }
}
