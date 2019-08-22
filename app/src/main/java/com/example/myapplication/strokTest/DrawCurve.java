package com.example.myapplication.strokTest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.example.myapplication.strokTest.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.strokTest.utils.MathUtils.calPointBySlope;

public class DrawCurve {
    private List<TimePoint> points = new ArrayList<>();
    private PointF lastTopPoint = new PointF();
    private PointF lastBottomPoint = new PointF();
    private Path strokePath = new Path();
    private boolean flag = true;


    public void onMove(MotionEvent event, Canvas canvas, Paint paint) {
        paint.setStrokeWidth(2);
        paint.setColor(Color.GREEN);
        strokePath.reset();
        TimePoint point = new TimePoint(event.getX(), event.getY(), event.getPressure());
        points.add(point);
        PointF pA = new PointF(), pB = new PointF(), pC = new PointF(), pD = new PointF(),
                pMT = new PointF(), pMB = new PointF();
        PointF pTop = new PointF(), pBottom = new PointF();
        if (points.size() == 2) {
            float k;
            k = (points.get(0).y - points.get(1).y) / (points.get(0).x - points.get(1).x);
            k = -1 / k;
            calPointBySlope(points.get(0), k, pA, pB, false);
            calPointBySlope(points.get(1), k, pC, pD,false);

            lastTopPoint.set((pA.x + pC.x) / 2, (pA.y + pC.y) / 2);
            lastBottomPoint.set((pB.x + pD.x) / 2, (pB.y + pD.y) / 2);
        } else if (points.size() >= 3) {
            float k;
            k = (points.get(1).y - points.get(2).y) / (points.get(1).x - points.get(2).x);
            k = -1 / k;

            changeFlag(points.get(0), points.get(1), points.get(2));

            //计算梯形四个点
            calPointBySlope(points.get(1), k, pA, pB, !flag);
            calPointBySlope(points.get(2), k, pC, pD,!flag);

            //计算梯形两腰中点
            pTop.set((pA.x + pC.x) / 2, (pA.y + pC.y) / 2);
            pBottom.set((pB.x + pD.x) / 2, (pB.y + pD.y) / 2);


            //计算中间夹角对应的点
            MathUtils.calAngleBisectorPoints(points.get(0), points.get(1), points.get(2), pMT, pMB);

            if (!MathUtils.isSameSide(points.get(1).x, points.get(1).y, points.get(2).x, points.get(2).y,
                    pTop.x, pTop.y, pMT.x, pMT.y)) {
                PointF temp = pMT;
                pMT = pMB;
                pMB = temp;
            }

            paint.setColor(Color.GREEN);
            canvas.drawLine(pMT.x, pMT.y, pMB.x, pMB.y, paint);
            canvas.drawText("PMT", pMT.x, pMT.y, paint);

            paint.setColor(Color.RED);
            canvas.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y, paint);

            paint.setColor(Color.BLACK);
            canvas.drawLine(pTop.x, pTop.y, pBottom.x, pBottom.y, paint);
            canvas.drawText("Topx", pTop.x, pTop.y, paint);

            strokePath.moveTo(lastTopPoint.x, lastTopPoint.y);
            strokePath.quadTo(pMT.x, pMT.y, pTop.x, pTop.y);
            strokePath.moveTo(lastBottomPoint.x, lastBottomPoint.y);
            strokePath.quadTo(pMB.x, pMB.y, pBottom.x, pBottom.y);
            paint.setColor(Color.BLUE);
            canvas.drawPath(strokePath, paint);

            points.remove(0);
            lastTopPoint = pTop;
            lastBottomPoint = pBottom;
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawPoint(event.getX(), event.getY(), paint);
    }


    private void changeFlag(TimePoint p0, TimePoint p1, TimePoint p2) {

        if (p0.x < p1.x && p0.y >= p1.y && p2.y > p1.y ) {
            flag = !flag;
        }
        if (p0.x > p1.x && p0.y < p1.y && p2.y < p1.y) {
            flag = !flag;
        }
        if (p0.x < p1.x && p0.y < p1.y && p2.y < p1.y) {
            flag = !flag;
        }
        if (p0.x > p1.x && p0.y > p1.y && p2.y > p1.y) {
            flag = !flag;
        }
    }

    public void init() {
        points.clear();
    }

}
