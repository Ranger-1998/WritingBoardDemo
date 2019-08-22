package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AnnotationView extends View {

    private Paint paint;
    private Path path;
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint bitmapPaint;
    private float startX, startY;
    public AnnotationView(Context context) {
        this(context, null);
    }

    public AnnotationView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnnotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                move(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                end();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    private void start(float x, float y){
        path.reset();
        path.moveTo(x, y);
        startX = x;
        startY = y;
    }

    private void move(float x, float y) {
        if (Math.abs(x - startX) >= 4 || Math.abs(y - startY) >= 4) {
            path.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
            startX = x;
            startY = y;
        }
    }

    private void end() {
        path.lineTo(startX, startY);
        canvas.drawPath(path, paint);
        path.reset();
    }

}
