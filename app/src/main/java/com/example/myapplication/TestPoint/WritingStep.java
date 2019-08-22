package com.example.myapplication.TestPoint;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 保存每一步的path和paint信息
 */
public class WritingStep {
    private Paint paint;
    private Path path;
    private PointF leftTop, rightBottom;
    private ArrayList<PointF> points = null; //笔迹经过的点集
    private ArrayList<HandwritingItem> handwritingItems = new ArrayList<>(); //保存笔迹的每一段，绘制简单笔锋效果。

    private HashSet<PointF> pointHashSet = new HashSet<>(); //旧的计算点集的方法


    public WritingStep(Path path, Paint paint){
        this.paint = paint;
        this.path = path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
        updatePoints(40);
    }

    public void setLeftTop(float x, float y) {
        leftTop = new PointF(x, y);
    }

    public void setRightBottom(float x, float y) {
        rightBottom = new PointF(x, y);
    }

    //更新边界
    public void updateBounds(float x, float y) {
        if (x < leftTop.x) leftTop.x = x;
        else if (x > rightBottom.x) rightBottom.x = x;

        if (y < leftTop.y) leftTop.y = y;
        else if (y > rightBottom.y) rightBottom.y = y;
    }

    public void setPoints(ArrayList<PointF> points) {
        this.points = points;
    }

    public boolean inArea(float x, float y){
        for (PointF point : getPoints()) {
            if (x - 50 < point.x && x + 50 > point.x && y - 50 < point.y && y + 50 > point.y) {
                return true;
            }
        }
        return false;
    }

    public PointF getLeftTop() {
        return leftTop;
    }

    public PointF getRightBottom() {
        return rightBottom;
    }

    public void updatePoints(int eLen) {
        int pointCount = 0;

        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        pointCount = (int) (length / eLen);
        PointF[] pointArray = new PointF[pointCount];
        float distance = 0f;
        int counter = 0;
        float[] aCoordinates = new float[2];

        while ((distance < length) && (counter < pointCount)) {
            pm.getPosTan(distance, aCoordinates, null);
            pointArray[counter] = new PointF(aCoordinates[0],
                    aCoordinates[1]);
            counter++;
            distance = distance + eLen;
        }
        points = new ArrayList<>();
        points.addAll(Arrays.asList(pointArray));
    }

    /**
     * 通过path获取经过的点集
     * @return 点集
     */
    public List<PointF> getPoints() {
        return points;
    }

    public HashSet<PointF> getPointHashSet() {
        return pointHashSet;
    }

    public void setPointHashSet(HashSet<PointF> pointHashSet) {
        this.pointHashSet = pointHashSet;
    }

    public ArrayList<HandwritingItem> getHandwritingItems() {
        return handwritingItems;
    }

    public void setHandwritingItems(ArrayList<HandwritingItem> handwritingItems) {
        this.handwritingItems = handwritingItems;
    }
}
