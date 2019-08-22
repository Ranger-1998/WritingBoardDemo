package com.example.myapplication.penNew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class WritingBoard extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private Paint paint; //画笔
    private Paint eraserPaint; //板擦画笔
    private Paint selectPaint; //圈选画笔
    private Paint markPaint; //标记画笔
    private Path path;
    private WritingStep curStep; //当前笔迹信息
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint bitmapPaint;
    private float startX, startY;
    private WritingState writingState;
    private boolean isEraser = false;
    private boolean isSelect = false;

    private Path selectRectPath; //圈选框的path
    private RectF selectRect; //圈选的外接矩形，方便判断触点

    private float scale = 0;  //记录旧的缩放标记，判断是放大还是缩小

    //标记被圈选的笔迹是否在操作中
    private boolean isOp = false;

    private boolean isRoam = false; //标记是否处于漫游功能

    private boolean isDrawing = true;

    private SurfaceHolder surfaceHolder;

    private SteelPen pen;

    private Queue<Operation> operationQueue = new LinkedBlockingDeque<>();


    public WritingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    /**
     * 画笔初始化
     */
    private void init() {

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        writingState = WritingState.getInstance();
        path = new Path();
        update();
        //初始化板擦
        eraserPaint = new Paint();
        eraserPaint.setColor(Color.BLACK);
        eraserPaint.setAntiAlias(true);
        eraserPaint.setDither(true);
        eraserPaint.setStyle(Paint.Style.FILL);

        //初始化圈选画笔
        selectPaint = new Paint();
        selectPaint.setColor(Color.RED);
        selectPaint.setStrokeWidth(4);
        selectPaint.setAntiAlias(true);
        selectPaint.setDither(true);
        selectPaint.setStyle(Paint.Style.STROKE);

        //初始化标记画笔
        markPaint = new Paint();
        markPaint.setColor(Color.RED);
        markPaint.setStrokeWidth(10);
        markPaint.setAntiAlias(true);
        markPaint.setDither(true);
        markPaint.setStrokeJoin(Paint.Join.ROUND);
        markPaint.setStrokeCap(Paint.Cap.ROUND);
        markPaint.setStyle(Paint.Style.STROKE);

        //初始化带有笔锋的画笔
        pen = new SteelPen(paint);
    }

    public Paint getPaint() {
        return paint;
    }

    /**
     * 更新画笔
     */
    public void update() {
        paint = new Paint();
        paint.setColor(writingState.getPenColor());
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(writingState.getPenSize());
        paint.setStyle(Paint.Style.STROKE);
        pen = new SteelPen(paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                operationQueue.offer(new MotionEventDown(event));
                break;
            case MotionEvent.ACTION_MOVE:
                operationQueue.offer(new MotionEventMove(event));
                break;
            case MotionEvent.ACTION_UP:
                operationQueue.offer(new MotionEventUp(event));
                break;
        }
        return true;
    }

    private void draw() {
        try {
            canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE);
                while (!operationQueue.isEmpty()) {
                    operationQueue.poll().doOperation();
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    /**
     * 移动和缩放画布
     *
     * @param dx  变换后的y坐标
     * @param dy  变换后的x坐标
     * @param d   缩放标记
     * @param mid 手指中心点
     */
    private void transformCanvas(float dx, float dy, float d, PointF mid) {
        clearCanvas();
        float s = 1;
        if (d != 0) {
            float dif = d - scale;
            Log.d("缩放的差", dif + "");
            if (Math.abs(dif) >= 10) {
                if (dif > 0) {
                    s = 1.02f;
                } else {
                    s = 0.98f;
                }
                scale = d;
            }
        }

        if (mid != null) {
            for (WritingStep step : writingState.getSteps()) {
                Matrix matrix = new Matrix();
                matrix.setTranslate(dx, dy);
                matrix.preScale(s, s, mid.x, mid.y);
                step.updatePoints(40);
                step.getPath().transform(matrix);
            }

            for (BitmapStep step : writingState.getBitmapSteps()) {
                Matrix matrix = new Matrix();
                matrix.setTranslate(dx, dy);
                matrix.preScale(s, s, mid.x, mid.y);
                step.getMatrix().postTranslate(dx, dy);
                step.getMatrix().postScale(s, s, mid.x, mid.y);
                step.getPath().transform(matrix);
            }
        }

        repaintAll();
    }

    private void moveSelectPath(float dx, float dy, float d, PointF mid) {
        clearCanvas();
        float s = 1;
        if (d != 0) {
            float dif = d - scale;

            if (Math.abs(dif) > 8) {
                if (dif > 0) {
                    s = 1.02f;
                } else {
                    s = 0.98f;
                }
                scale = d;
            }
        }

        for (WritingStep step : writingState.getSelectedSteps()) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(dx, dy);
            if (mid != null) {
                matrix.preScale(s, s, mid.x, mid.y);
            }
            step.getPath().transform(matrix);
            step.getPen().transform(matrix);
            markPaint.setStrokeWidth(step.getPaint().getStrokeWidth() + 10);
            canvas.drawPath(step.getPath(), markPaint);
        }

        for (BitmapStep step : writingState.getSelectBitmap()) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(dx, dy);
            if (mid != null) {
                matrix.preScale(s, s, mid.x, mid.y);
            }
            step.getPath().transform(matrix);

            step.getMatrix().postTranslate(dx, dy);
            if (mid != null)
                step.getMatrix().postScale(s, s, mid.x, mid.y);

            markPaint.setStrokeWidth(10);
            canvas.drawPath(step.getPath(), markPaint);
        }


        //绘制圈选的最小外接矩形
        selectRect = new RectF();
        Matrix m = new Matrix();
        selectRectPath.computeBounds(selectRect, true);
        m.setTranslate(dx, dy);
        if (mid != null) {
            m.preScale(s, s, mid.x, mid.y);
        }
        selectRectPath.transform(m);
        canvas.drawPath(selectRectPath, selectPaint);

        repaintAll();
    }


    /**
     * 擦除一条线
     *
     * @param x 触点x
     * @param y 触点y
     */
    private void eraseALine(float x, float y) {
        Iterator<WritingStep> it = writingState.getSteps().iterator();
        while (it.hasNext()) {
            WritingStep step = it.next();
            if (step.inArea(x, y)) {
                it.remove();
                writingState.getDeleteSteps().add(step);
                clearCanvas();
                repaintAll();
            }
        }
    }

    /**
     * 圈选笔迹
     */
    private void selectStep() {

        Path selectPath = new Path();

        clearCanvas();
        writingState.getSelectedSteps().clear();
        writingState.getSelectBitmap().clear();

        for (WritingStep step : writingState.getSteps()) {
            Path temp = new Path();
            temp.addPath(path);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                temp.op(step.getPath(), Path.Op.INTERSECT);
                if (!temp.isEmpty()) {
                    selectPath.addPath(step.getPath());
                    writingState.getSelectedSteps().add(step);
                    markPaint.setStrokeWidth(step.getPaint().getStrokeWidth() + 10);
                    canvas.drawPath(step.getPath(), markPaint);
                }
            }
        }

        for (BitmapStep step : writingState.getBitmapSteps()) {
            Path temp = new Path();
            temp.addPath(path);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                temp.op(step.getPath(), Path.Op.INTERSECT);
                if (!temp.isEmpty()) {
                    selectPath.addPath(step.getPath());
                    writingState.getSelectBitmap().add(step);
                    markPaint.setStrokeWidth(10);
                    canvas.drawPath(step.getPath(), markPaint);
                }
            }
        }

        if (!selectPath.isEmpty()) {
            //计算圈选path总边界
            selectRect = new RectF();
            selectPath.computeBounds(selectRect, true);
            selectRectPath = new Path();
            expandRect(selectRect);
            selectRectPath.addRect(selectRect, Path.Direction.CCW);
            canvas.drawPath(selectRectPath, selectPaint);
        }
        repaintAll();
    }

    /**
     * 扩大圈选框
     *
     * @param r 圈选框
     */
    private void expandRect(RectF r) {
        int e = 40;
        r.left -= e;
        r.right += e;
        r.top -= e;
        r.bottom += e;
    }

    /**
     * 清除画布
     */
    private void clearCanvas() {
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(p);
    }

    /**
     * 重绘所有笔迹
     */
    private void repaintAll() {
        Log.d("现存的笔迹数量", writingState.getSteps().size() + "");
        for (WritingStep step : writingState.getSteps()) {
            step.getPen().draw(canvas);
        }
    }

    /**
     * 判断是否在圈选状态
     *
     * @return select
     */
    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    /**
     * 退出圈选状态
     */
    public void exitSelect() {
        clearCanvas();
        repaintAll();
        selectRect = null;
        selectRectPath = null;
    }

    /**
     * 计算多个点之间的最大距离
     *
     * @param points 点集
     * @return 距离
     */
    private float touchDistance(List<PointF> points) {
        if (points.size() <= 1) return 0;
        float max = 0;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                float d = calDistance(points.get(i), points.get(j));
                if (d > max) {
                    max = d;
                }
            }
        }
        return max;
    }

    /**
     * 计算两点之间的浮点距离
     *
     * @param p1 点1
     * @param p2 点2
     * @return 距离
     */
    private float calDistance(PointF p1, PointF p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 获取所有触点的中间点
     *
     * @param points 触点集合
     * @return 中间点
     */
    private PointF getMidPoint(List<PointF> points) {
        if (points.size() <= 1) return null;
        float max = 0;
        PointF ps = null;
        PointF pe = null;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                float d = calDistance(points.get(i), points.get(j));
                if (d > max) {
                    ps = points.get(i);
                    pe = points.get(j);
                    max = d;
                }
            }
        }
        if (ps != null && pe != null) {
            return new PointF((ps.x + pe.x) / 2, (ps.y + pe.y) / 2);
        } else return null;
    }

    /**
     * 增加一个图片进手写板
     *
     * @param bitmap 图片的bitmap
     */
    public void addBitmap(Bitmap bitmap) {
        writingState.getBitmapSteps().add(new BitmapStep(bitmap, new Matrix()));
        clearCanvas();
        repaintAll();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }

    @Override
    public void run() {
        while (isDrawing) {
            draw();
        }
    }

    public enum EraseType {
        ERASE_LINE_ALL,
        ERASE_LINE_SEC
    }

    /**
     * 测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (wSpecMode == MeasureSpec.AT_MOST && hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, hSpecSize);
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wSpecSize, 300);
        }
    }

    abstract class Operation {
        protected MotionEvent event;
        Operation(MotionEvent e){
            event = e;
        }
        abstract void doOperation();
    }

    class MotionEventDown extends Operation {

        MotionEventDown(MotionEvent e) {
            super(e);
        }

        @Override
        public void doOperation() {
            float x = event.getX();
            float y = event.getY();

            if (isSelect) {
                if (selectRect != null && selectRect.contains(x, y)) {
                    isOp = true;
                } else {
                    isOp = false;
                    selectRect = new RectF();
                    selectRectPath = new Path();
                }
            } else {
                Log.d("手指按下的数量", event.getPointerCount() + "个");
                //如果不在圈选状态下，二指以上可触发漫游功能。
                if (event.getPointerCount() > 1) {
                    isRoam = true;
                }
            }
            if (!isEraser) {
                if (!isOp) {
                    path = new Path();
                    path.moveTo(x, y);
                    if (!isSelect) {
                        curStep = new WritingStep(path, paint);
                    }
                }
            }

            startX = x;
            startY = y;
        }
    }

    class MotionEventMove extends Operation {

        MotionEventMove(MotionEvent e) {
            super(e);
        }

        @Override
        public void doOperation() {
            float x = event.getX(0);
            float y = event.getY(0);
            if (isEraser) {
                eraseALine(x, y);
            } else {
                if (Math.abs(x - startX) >= 4 || Math.abs(y - startY) >= 4) {
                    List<PointF> points = new ArrayList<>();
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        points.add(new PointF(event.getX(i), event.getY(i)));
                    }
                    float d = touchDistance(points);
                    if (isOp) moveSelectPath(x - startX, y - startY, d, getMidPoint(points));
                    else if (isRoam) {
                        transformCanvas(x - startX, y - startY, d, getMidPoint(points));
                        curStep = null;
                    }
                    if (path != null) {
                        path.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
                    }

                }
            }
            startX = x;
            startY = y;
        }
    }

    class MotionEventUp extends Operation {

        MotionEventUp(MotionEvent e) {
            super(e);
        }

        @Override
        public void doOperation() {
            if (!isEraser) {
                Log.d("选择和操作", isSelect ? "选择了" : "没选择");
                if (isSelect) {
                    Log.d("选择和操作", isOp ? "操作中" : "未操作");
                    if (event.getPointerId(event.getActionIndex()) == 0) {
                        if (!isOp) {
                            canvas.drawPath(path, selectPaint);
                            selectStep();
                        } else {
                            isOp = false;
                        }
                    }
                    writingState.updateSteps();
                } else {
                    if (isRoam) {
                        isRoam = false;
                    } else {
                        if (path != null && curStep != null && pen != null) {
                            //canvas.drawPath(path, paint);
                            curStep.setPaint(paint);
                            curStep.setPath(path);
                            curStep.setPen(pen);
                            writingState.getSteps().add(curStep);
                            curStep = null;
                            pen.draw(canvas);
                            pen.setFirst(false);
                            pen = new SteelPen(paint);
                        }
                    }
                }
                path = null;
            } else {
                isEraser = false;
            }

        }
    }
}

