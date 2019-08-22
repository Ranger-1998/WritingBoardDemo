package com.example.myapplication.pen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;


public class SteelPen {

    private ArrayList<ControllerPoint> controllerPoints =new ArrayList<>(); //需要绘制的控制点集合
    private ArrayList<ControllerPoint> locusPoints = new ArrayList<>(); //记录点的轨迹
    private ControllerPoint lastPoint = new ControllerPoint(0, 0);
    private Paint mPaint;
    //笔的宽度信息
    private double mBaseWidth;
    private Bezier mBezier = new Bezier();

    private double mLastVel;

    private double mLastWidth;

    private ControllerPoint mCurPoint;


    public void setPaint(Paint paint) {
        mPaint = paint;
        mBaseWidth = 100;
    }

    public void draw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        //点的集合少 不去绘制
        if (controllerPoints == null || controllerPoints.size() < 1)
            return;

        mCurPoint = controllerPoints.get(0);
        drawPoints(canvas);

    }

    public boolean onTouchEvent(MotionEvent event, Canvas canvas) {
        // event会被下一次事件重用，这里必须生成新的，否则会有问题
        MotionEvent event2 = MotionEvent.obtain(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onDown(createMotionElement(event2));
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(createMotionElement(event2));
                return true;
            case MotionEvent.ACTION_UP:
                onUp(createMotionElement(event2),canvas);
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * 按下的事件
     * @param mElement
     */
    private void onDown(MotionElement mElement){

        locusPoints.clear();
        //如果在brush字体这里接受到down的事件，把下面的这个集合清空的话，那么绘制的内容会发生改变
        controllerPoints.clear();
        //记录down的控制点的信息
        ControllerPoint curPoint = new ControllerPoint(mElement.x, mElement.y);

        mLastWidth = 0.8 * mBaseWidth;

        //down下的点的宽度
        curPoint.width = (float) mLastWidth;
        mLastVel = 0;
        locusPoints.add(curPoint);
        //记录当前的点
        lastPoint = curPoint;
    }


    /**
     * 手指移动的事件
     */
    private void onMove(MotionElement mElement){

        ControllerPoint curPoint = new ControllerPoint(mElement.x, mElement.y);
        double deltaX = curPoint.x - lastPoint.x;
        double deltaY = curPoint.y - lastPoint.y;
        //deltaX和deltay平方和的二次方根 想象一个例子 1+1的平方根为1.4 （x²+y²）开根号
        //同理，当滑动的越快的话，deltaX+deltaY的值越大，这个越大的话，curDis也越大
        double curDis = Math.hypot(deltaX, deltaY);
        //我们求出的这个值越小，画的点或者是绘制椭圆形越多，这个值越大的话，绘制的越少，笔就越细，宽度越小
        double curVel = curDis * IPenConfig.DIS_VEL_CAL_FACTOR;
        double curWidth;
        //点的集合少，我们得必须改变宽度,每次点击的down的时候，这个事件
        if (locusPoints.size() < 2) {
            if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = mElement.pressure * mBaseWidth;
            } else {
                curWidth = calcNewWidth(curVel, mLastVel
                );
            }
            curPoint.width = (float) curWidth;
            mBezier.init(lastPoint, curPoint);
        } else {
            mLastVel = curVel;
            if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = mElement.pressure * mBaseWidth;
            } else {
                curWidth = calcNewWidth(curVel, mLastVel);
            }
            curPoint.width = (float) curWidth;
            mBezier.addNode(curPoint);
        }
        //每次移动的话，这里赋值新的值
        mLastWidth = curWidth;
        locusPoints.add(curPoint);
        movePoints(curDis);
        lastPoint = curPoint;
    }


    /**
     * 手指抬起来的事件
     */
    private void onUp(MotionElement mElement, Canvas canvas){

        mCurPoint = new ControllerPoint(mElement.x, mElement.y);
        double deltaX = mCurPoint.x - lastPoint.x;
        double deltaY = mCurPoint.y - lastPoint.y;
        double curDis = Math.hypot(deltaX, deltaY);
        //如果用笔画的画我的屏幕，记录他宽度的和压力值的乘，但是哇，这个是不会变的
        if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
            mCurPoint.width = (float) (mElement.pressure * mBaseWidth);
        } else {
            mCurPoint.width = 0;
        }

        locusPoints.add(mCurPoint);

        mBezier.addNode(mCurPoint);

        int steps = 1 + (int) curDis / IPenConfig.STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = mBezier.getPoint(t);
            controllerPoints.add(point);
        }
        //
        mBezier.end();
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = mBezier.getPoint(t);
            controllerPoints.add(point);
        }

        // 手指up 我画到纸上上
        draw(canvas);
    }


    private double calcNewWidth(double curVel, double lastVel) {
        double calVel = curVel * 0.6 + lastVel * 0.4;
        //返回指定数字的自然对数
        //手指滑动的越快，这个值越小，为负数
        double vfac = Math.log(3) * (-calVel);
        //此方法返回值e，其中e是自然对数的基数。
        //Math.exp(vfac) 变化范围为0 到1 当手指没有滑动的时候 这个值为1 当滑动很快的时候无线趋近于0
        //在次说明下，当手指抬起来，这个值会变大，这也就说明，抬起手太慢的话，笔锋效果不太明显
        //这就说明为什么笔锋的效果不太明显

        return mBaseWidth * Math.exp(vfac);
    }

    /**
     * event.getPressure(); //LCD可以感应出用户的手指压力，当然具体的级别由驱动和物理硬件决定的,我的手机上为1
     */
    private MotionElement createMotionElement(MotionEvent motionEvent) {
        return new MotionElement(motionEvent.getX(), motionEvent.getY(),
                motionEvent.getPressure(), motionEvent.getToolType(0));
    }

    /**
     * 当现在的点和触摸点的位置在一起的时候不用去绘制
     * 但是这里也可以优化，当一直处于onDown事件的时候，其实这个方法一只在走
     */
    private void drawToPoint(Canvas canvas, ControllerPoint point, Paint paint) {

    }


    private void movePoints(double curDis) {
        int steps = 1 + (int) curDis / IPenConfig.STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = mBezier.getPoint(t);
            controllerPoints.add(point);
        }
    }


    private void drawPoints(Canvas canvas) {
        for (int i = 1; i < controllerPoints.size(); i++) {
            ControllerPoint point = controllerPoints.get(i);
            if ((mCurPoint.x != point.x) || (mCurPoint.y != point.y)) {
                drawLine(canvas, mCurPoint.x, mCurPoint.y, mCurPoint.width, point.x,
                        point.y, point.width, mPaint);
            }
            mCurPoint = point;
        }
    }

    private void drawLine(Canvas canvas, double x0, double y0, double w0, double x1, double y1, double w1, Paint paint) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个园
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

        for (int i = 0; i < steps; i++) {
            //都是用于表示坐标系中的一块矩形区域，并可以对其做一些简单操作
            //精度不一样。Rect是使用int类型作为数值，RectF是使用float类型作为数值。
            RectF oval = new RectF();
            oval.set((float) (x - w / 4.0f), (float) (y - w / 2.0f), (float) (x + w / 4.0f), (float) (y + w / 2.0f));
            //最基本的实现，通过点控制线，绘制椭圆
            canvas.drawOval(oval, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
        }
    }

}
