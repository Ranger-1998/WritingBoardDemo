package com.example.myapplication.gradienttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class TestRect extends View {
    PointF p1 = new PointF();
    PointF p2 = new PointF();
    Paint paint;
    ArrayList<RectF> rects = new ArrayList<>();
    Bitmap bitmap;
    Canvas canvas;
    public TestRect(Context context) {
        this(context, null);
    }

    public TestRect(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TestRect(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                p1.x = event.getX();
                p1.y = event.getY();
                p2.x = p1.x;
                p2.y = p1.y;
                break;
            case MotionEvent.ACTION_MOVE:
                p2.x = event.getX();
                p2.y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                p2.x = event.getX();
                p2.y = event.getY();
                canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint);
                rects.add(new RectF(p1.x, p1.y, p2.x, p2.y));

        }
        invalidate();
        return true;
    }
}
