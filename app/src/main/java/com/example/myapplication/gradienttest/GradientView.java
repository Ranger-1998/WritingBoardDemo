package com.example.myapplication.gradienttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GradientView extends View {
    Paint paint;
    PointF center;
    Bitmap bitmap;
    Paint eraser;
    Path eraserPath;
    PointF lastPoint = new PointF();
    Canvas bitmapCanvas;
    Paint hisPaint;

    public GradientView(Context context) {
        this(context, null);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        eraser = new Paint();
        //eraser.setColor(Color.BLACK);
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setStrokeWidth(10);
        eraserPath = new Path();

        hisPaint = new Paint();
        hisPaint.setColor(Color.BLACK);
        hisPaint.setStyle(Paint.Style.FILL);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        center = new PointF(w/ 2f, h / 2f);
        bitmap = createColorWheelBitmap(w, h);
        bitmapCanvas = new Canvas(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);


        canvas.drawBitmap(bitmap, 0, 0, null);
        //canvas.drawPath(eraserPath, eraser);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int size = event.getHistorySize();
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < size; i++) {
            for (int p = 0; p < pointerCount; p++) {
                float x = event.getHistoricalX(p, i);
                float y = event.getHistoricalY(p, i);
                bitmapCanvas.drawRect(x - 50, y - 50,
                        x + 50, y + 50, hisPaint);
            }
        }
        invalidate();
        return true;
    }

    private Bitmap createColorWheelBitmap(int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int[] colors = new int[colorCount + 1];
        float[] hsv = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];

        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, 300, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        paint.setShader(composeShader);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, 300, paint);
        return bitmap;

    }
}
