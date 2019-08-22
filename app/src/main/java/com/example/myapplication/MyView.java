package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.example.myapplication.pen.SteelPen;

public class MyView extends View {
        private Paint mPaint;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private SparseArray<Path> mPath = new SparseArray<>();
        private Paint mBitmapPaint;
        private SparseArray<PointF> points = new SparseArray<>();


        public MyView(Context context) {
            this(context, null);
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);// 抗锯齿
            mPaint.setDither(true); // 防抖动
            mPaint.setColor(0xFFFF0000);// 设置颜色
            mPaint.setStyle(Paint.Style.STROKE);// 画笔类型 STROKE空心 FILL 实心
            mPaint.setStrokeJoin(Paint.Join.ROUND);// 画笔接洽点类型 如影响矩形但角的外轮廓,让画的线圆滑
            mPaint.setStrokeCap(Paint.Cap.ROUND);// 画笔笔刷类型 如影响画笔但始末端
            mPaint.setStrokeWidth(30);// 设置线宽
            mPath.clear();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);// 是使位图进行有利的抖动的位掩码标志
            mPaint.setStyle(Paint.Style.STROKE);

        }

    @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (int i = 0; i < mPath.size(); i++) {
                int k = mPath.keyAt(i);
                canvas.drawPath(mPath.get(k), mPaint);
            }
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }

        private void touch_start(float x, float y, MotionEvent event) {
            int index = event.getPointerId(event.getActionIndex());

            mPath.put(index, new Path());
            mPath.get(index).moveTo(x, y);
            points.put(index, new PointF(x, y));
        }

        private void touch_move(MotionEvent event) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                int index = event.getPointerId(i);
                float x = event.getX(i);
                float y = event.getY(i);
                Log.d("手指追踪", index + "");
                float dx = Math.abs(x - points.get(index).x);
                float dy = Math.abs(y - points.get(index).y);
                if (dx >= 4 || dy >= 4) {
                    /**
                     * quadTo方法的实现是当我们不仅仅是画一条线甚至是画弧线时会形成平滑的曲线，
                     * 该曲线又称为"贝塞尔曲线"(Beziercurve) 其中，x1，y1为控制点的坐标值，x2，y2为终点的坐标值；
                     */
                    mPath.get(index).quadTo(points.get(index).x, points.get(index).y,
                            (x + points.get(index).x) / 2, (y + points.get(index).y) / 2);
                    mCanvas.drawPath(mPath.get(index), mPaint);
                    points.get(index).x = x;
                    points.get(index).y = y;
                }
            }
        }

        private void touch_up(MotionEvent event) {
            int index = event.getPointerId(event.getActionIndex());
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                points.clear();
                mPath.clear();
            } else {
                points.remove(index);
                mPath.remove(index);
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX(event.getActionIndex());
            float y = event.getY(event.getActionIndex());
            //MotionEvent e = MotionEvent.obtain(event);
            //pen.onTouchEvent(e, mCanvas);

            switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPath.clear();
            case MotionEvent.ACTION_POINTER_DOWN:
                touch_start(x, y, event);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                touch_up(event);
                break;
            }
            invalidate();
            return true;
        }
    }