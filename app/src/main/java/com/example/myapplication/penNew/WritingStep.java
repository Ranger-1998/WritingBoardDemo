package com.example.myapplication.penNew;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存每一步的path和paint信息
 */
public class WritingStep{
    private Paint paint;
    private Path path;
    private ArrayList<PointF> points = null; //笔迹经过的点集
    private SteelPen pen;

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

    /**
     * 通过板擦的范围更新点集
     * @param eLen 板擦的范围（速度）
     */
    public void updatePoints(int eLen) {
        points = new ArrayList<>();
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        float distance = 0f;

        float[] aCoordinates = new float[2];

        while (distance < length) {
            pm.getPosTan(distance, aCoordinates, null);
            points.add(new PointF(aCoordinates[0],
                    aCoordinates[1]));
            distance = distance + eLen;
        }
    }

    /**
     * 通过path获取经过的点集
     * @return 点集
     */
    public List<PointF> getPoints() {
        return points;
    }

    public SteelPen getPen() {
        return pen;
    }

    public void setPen(SteelPen pen) {
        this.pen = pen;
    }
}
