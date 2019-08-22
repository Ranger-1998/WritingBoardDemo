package com.example.myapplication;
 
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
 
import java.util.ArrayList;

public class MapView extends View {
    public MapView(Context context) {
        this(context, null);
    }
 
    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
 
    }
 
    private void init(Context context) {
        map_pic = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        map_pic = Bitmap.createBitmap(map_pic, 0, 0, map_pic.getWidth(), map_pic.getHeight(), matrix, true);
 
        paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔
        paint.setColor(Color.BLACK);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
    }
 
    private Paint paint = null;
    private Bitmap map_pic = null;
    private Canvas canvas = null;
 
    private float dx = 0;
    private float dy = 0;
    private float rotate = 0;
    private float scale = 1;
 
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(dx, dy);
        canvas.scale(scale, scale, map_pic.getWidth() / 2, map_pic.getHeight() / 2);
        canvas.rotate(rotate, map_pic.getWidth() / 2, map_pic.getHeight() / 2);
 
        canvas.drawBitmap(map_pic, 0.0f, 0.0f, paint);
 
        this.canvas = canvas;
 
        for (PointF p : pointList) {
            canvas.drawCircle(p.x, p.y, 10, paint);
        }
        invalidate();
    }
 
 
    private ArrayList<PointF> pointList = new ArrayList<>();
 
    public void setPoint(float x, float y) {
 
        pointList.add(new PointF(x, y));
    }
 
 
    //以下代码为地图的手势控制
    private final int DRAG = 1;
    private final int ZOOM = 2;
    private final int NONE = 0;
    private int mode = NONE;
 
    private float old_x_down = 0;
    private float old_y_down = 0;
    private float old_translate_x = 0;
    private float old_translate_y = 0;
 
    private PointF start = new PointF();
    private PointF mid = new PointF();
 
    private float oldScale = 1;
    private float oldDist = 1f;
    private float oldAngle = 0;
    private float oldRotation = 0;
 
 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                old_x_down = event.getX();
                old_y_down = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldAngle = angle(event);
                midPoint(mid, event);
                break;
 
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    //缩放
                    float newDist = spacing(event);
                    float newScale = (newDist) / oldDist * oldScale;
 
                    Log.i("map缩放", "onTouchEvent: " + newScale + "  ");
//                    mapView.setScaleX(newScale);
//                    mapView.setScaleY(newScale);
                    scale = newScale;
 
                    oldDist = newDist;
                    oldScale = newScale;
 
                    //旋转
                    float newAngle = angle(event);
                    float newRotation = newAngle - oldAngle + oldRotation;
                    Log.i("map旋转", "onTouch: " + newRotation + "  " + oldRotation);
 
//                    mapView.setRotation(newRotation);
                    rotate = newRotation;
 
                    oldRotation = newRotation;
                    oldAngle = newAngle;
 
 
                } else if (mode == DRAG) {
                    float new_d_x = event.getX() - old_x_down + old_translate_x;
                    float new_d_y = event.getY() - old_y_down + old_translate_y;
                    Log.i("map平移", "onTouchEvent: " + new_d_x + "  " + new_d_y);
 
//                    mapView.offsetLeftAndRight((int) (0.5 + d_x));
//                    mapView.offsetTopAndBottom((int) (0.5 + d_y));
                    dx = new_d_x;
                    dy = new_d_y;
 
 
                    old_x_down = event.getX();
                    old_y_down = event.getY();
                    old_translate_x = dx;
                    old_translate_y = dy;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }
 
    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
 
    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
 
    // 取旋转角度
    private float angle(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
 
 
}

