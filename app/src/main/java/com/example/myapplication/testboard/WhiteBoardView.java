package com.example.myapplication.testboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by shutup on 16/7/9.
 */
public class WhiteBoardView extends View {

    private MainActivity mActivity = null;

    private static final float TOUCH_TOLERANCE = 1;
    private Path mPath = null;
    private Paint mPaint = null;
    private Paint mBitmapPaint = null;
    private float mX = 0;
    private float mY = 0;
    private int screenW = 0;
    private int screenH = 0;

    private Canvas mCanvas;
    private Bitmap mBitmap;


    private PathSegment pathSegment = null;

    private WhiteBoardViewCurrentState state = null;

    public WhiteBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (MainActivity) context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenH = displayMetrics.heightPixels;
        screenW = displayMetrics.widthPixels;

        initBoard();
        initCanvas();
    }

    public void loadPaint(){
        state = WhiteBoardViewCurrentState.getInstance();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(state.getPenColor());
        mPaint.setStrokeWidth(state.getPenSize());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(state.getPenCap());
    }

    private void initBoard(){

        loadPaint();

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmap = Bitmap.createBitmap(screenW,screenH,Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitmap);
    }

    private void initCanvas() {
        mCanvas.drawColor(state.getBoardColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
        if (mPath != null){
            canvas.drawPath(mPath,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int type = event.getAction();
        float x = event.getRawX();
        float y = event.getRawY();
        if (type == MotionEvent.ACTION_DOWN){
            mPath = new Path();
            pathSegment = new PathSegment(mPaint,mPath);


            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }else if(type == MotionEvent.ACTION_MOVE){
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                /**
                 * quadTo方法的实现是当我们不仅仅是画一条线甚至是画弧线时会形成平滑的曲线，
                 * 该曲线又称为"贝塞尔曲线"(Beziercurve) 其中，x1，y1为控制点的坐标值，x2，y2为终点的坐标值；
                 */
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }else if(type == MotionEvent.ACTION_UP){
            mPath.lineTo(x,y);
            mCanvas.drawPath(mPath,mPaint);
            state.getSavePaths().add(pathSegment);
            mPath = null;

            mActivity.checkPathStackSize(state);
        }
        invalidate();
        return true;
    }



    public void resetBoard(){
        initCanvas();
        state.getSavePaths().clear();
        state.getDeletePaths().clear();
        invalidate();
    }

    public void redo(){
        if (state.getDeletePaths().size() > 0){
            PathSegment pathSegment = state.getDeletePaths().get(state.getDeletePaths().size() - 1);
            mCanvas.drawPath(pathSegment.getPath(),pathSegment.getPaint());
            state.getDeletePaths().remove(state.getDeletePaths().size() - 1);
            state.getSavePaths().add(pathSegment);
            invalidate();
        }
    }

    public void undo(){
        if (state.getSavePaths().size() > 0) {
            PathSegment pathSegment = state.getSavePaths().get(state.getSavePaths().size() - 1);
            state.getDeletePaths().add(pathSegment);
            state.getSavePaths().remove(state.getSavePaths().size() - 1);
            initCanvas();
            for (PathSegment p:state.getSavePaths()
                 ) {
                mCanvas.drawPath(p.getPath(), p.getPaint());
            }
            invalidate();
        }
    }

}
