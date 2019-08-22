package com.example.myapplication.penNew;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;


public class SteelPen {

    private ArrayList<ControllerPoint> controllerPoints = new ArrayList<>(); //需要绘制的控制点集合
    private ArrayList<ControllerPoint> locusPoints = new ArrayList<>(); //记录点的轨迹
    private ControllerPoint lastPoint = new ControllerPoint(0, 0);
    private Path path;
    private Paint penPaint;
    //笔的宽度信息
    private double penWidth;
    private Bezier bezier = new Bezier();

    private boolean isFirst = true;

    private double speed;
    private double lastWidth;
    private ControllerPoint curPoint;

    public SteelPen(Paint paint) {
        this.penPaint = paint;
        penWidth = paint.getStrokeWidth();
        path = new Path();
    }

    public void draw(Canvas canvas) {
        if (isFirst) {
            penPaint.setStyle(Paint.Style.FILL);
            if (controllerPoints == null || controllerPoints.size() < 1)
                return;

            //curPoint = controllerPoints.get(0);
            curPoint = controllerPoints.get(0);
            Log.d("控制点的个数", controllerPoints.size() + "个" + "路径点的个数" + locusPoints.size());
            drawPoints(canvas);
        } else {
            canvas.drawPath(path, penPaint);
        }
    }

    public void onTouchEvent(MotionEvent event, Canvas canvas) {
        MotionEvent e = MotionEvent.obtain(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onDown(createMotionElement(e));
                return;
            case MotionEvent.ACTION_MOVE:
                onMove(createMotionElement(e));
                return;
            case MotionEvent.ACTION_UP:
                onUp(createMotionElement(e), canvas);
                return;
            default:
                break;
        }
    }

    /**
     * 按下的事件
     */
    private void onDown(MotionElement element) {

        locusPoints.clear();
        controllerPoints.clear();
        ControllerPoint curPoint = new ControllerPoint(element.getX(), element.getY());
        lastWidth = 0.8 * penWidth;
        curPoint.setWidth((float) lastWidth);
        speed = 0;
        locusPoints.add(curPoint);
        lastPoint = curPoint;
    }


    /**
     * 手指移动的事件
     */
    private void onMove(MotionElement element) {

        ControllerPoint curPoint = new ControllerPoint(element.getX(), element.getY());
        double deltaX = curPoint.getX() - lastPoint.getX();
        double deltaY = curPoint.getY() - lastPoint.getY();
        double curDis = Math.hypot(deltaX, deltaY);
        double curVel = curDis * 0.02; //当前速度，越大线越细，改变这个参数可调整线过细的问题。
        double curWidth;
        if (locusPoints.size() < 2) {
            if (element.getType() == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = element.getPressure() * penWidth;
            } else {
                curWidth = calWidth(curVel, speed);
            }
            curPoint.setWidth((float) curWidth);
            bezier.init(lastPoint, curPoint);
        } else {
            speed = curVel;
            if (element.getType() == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = element.getPressure() * penWidth;
            } else {
                curWidth = calWidth(curVel, speed);
            }
            curPoint.setWidth((float) curWidth);
            bezier.addNode(curPoint);
        }

        lastWidth = curWidth;
        locusPoints.add(curPoint);
        movePoints(curDis);
        lastPoint = curPoint;
    }


    /**
     * 手指抬起来的事件
     */
    private void onUp(MotionElement element, Canvas canvas) {

        curPoint = new ControllerPoint(element.getX(), element.getY());
        double deltaX = curPoint.getX() - lastPoint.getX();
        double deltaY = curPoint.getY() - lastPoint.getY();
        double curDis = Math.hypot(deltaX, deltaY);
        if (element.getType() == MotionEvent.TOOL_TYPE_STYLUS) {
            curPoint.setWidth((float) (element.getPressure() * penWidth));
        } else {
            curPoint.setWidth(0);
        }

        locusPoints.add(curPoint);

        bezier.addNode(curPoint);

        int steps = 1 + (int) curDis / 10;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = bezier.getPoint(t);
            controllerPoints.add(point);
        }
        bezier.end();
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = bezier.getPoint(t);
            controllerPoints.add(point);
        }
        draw(canvas);
    }

    private double calWidth(double curVel, double lastVel) {
        double calVel = curVel * 0.6 + lastVel * 0.4;
        double v = Math.log(3) * (-calVel);
        return penWidth * Math.exp(v);
    }

    private MotionElement createMotionElement(MotionEvent motionEvent) {
        return new MotionElement(motionEvent.getX(), motionEvent.getY(),
                motionEvent.getPressure(), motionEvent.getToolType(0));
    }

    private void movePoints(double curDis) {
        int steps = 1 + (int) curDis / 10;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = bezier.getPoint(t);
            controllerPoints.add(point);
        }
    }


    private void drawPoints(Canvas canvas) {
        for (int i = 1; i < controllerPoints.size(); i++) {
            ControllerPoint point = controllerPoints.get(i);
            if ((curPoint.getX() != point.getX()) || (curPoint.getY() != point.getY())) {
                drawLine(canvas, curPoint.getX(), curPoint.getY(), curPoint.getWidth(), point.getX(),
                        point.getY(), point.getWidth(), penPaint);
            }
            curPoint = point;
        }
    }

    private void drawLine(Canvas canvas, double x0, double y0, double w0, double x1, double y1, double w1, Paint paint) {
        //求两个数字的平方根
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int steps;
        if (paint.getStrokeWidth() < 6) {
            steps = 1 + (int) (curDis / 2);
        } else if (paint.getStrokeWidth() > 60) {
            steps = 1 + (int) (curDis / 4);
        } else {
            steps = 1 + (int) (curDis / 3);
        }
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;

        //根据计算的步数绘制椭圆
        for (int i = 0; i < steps; i++) {
            RectF oval = new RectF();
            oval.set((float) (x - w / 4.0f), (float) (y - w / 2.0f),
                    (float) (x + w / 4.0f), (float) (y + w / 2.0f));
            canvas.drawOval(oval, paint);
            path.addOval(oval, Path.Direction.CW);
            x += deltaX;
            y += deltaY;
            w += deltaW;
        }

    }

    public void transform(Matrix matrix) {
        path.transform(matrix);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }
}
