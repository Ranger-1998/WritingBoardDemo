package com.example.myapplication.penNew;

/**
 * 对点的位置和宽度控制的bezier曲线，主要是两个点，都包含了宽度和点的坐标
 */
public class Bezier {
    //控制点
    private ControllerPoint controllerPoint = new ControllerPoint();
    //距离
    private ControllerPoint desPoint = new ControllerPoint();
    //下一个控制点
    private ControllerPoint nextPoint = new ControllerPoint();
    //资源点
    private ControllerPoint sourcePoint = new ControllerPoint();

    public Bezier() {
    }

    /**
     * 初始化两个点，
     *
     * @param last 最后的点的信息
     * @param cur  当前点的信息
     */
    public void init(ControllerPoint last, ControllerPoint cur) {
        init(last.getX(), last.getY(), last.getWidth(), cur.getX(), cur.getY(), cur.getWidth());
    }

    private void init(float lastX, float lastY, float lastWidth, float x, float y, float width) {
        //资源点设置，最后的点的为资源点
        sourcePoint.set(lastX, lastY, lastWidth);
        float midX = getMid(lastX, x);
        float midY = getMid(lastY, y);
        float midW = getMid(lastWidth, width);
        //距离点为平均点
        desPoint.set(midX, midY, midW);
        //控制点为当前的距离点
        controllerPoint.set(getMid(lastX, midX), getMid(lastY, midY), getMid(lastWidth, midW));
        //下个控制点为当前点
        nextPoint.set(x, y, width);
    }

    public void addNode(ControllerPoint cur) {
        addNode(cur.getX(), cur.getY(), cur.getWidth());
    }

    /**
     * 替换就的点，原来的距离点变换为资源点，控制点变为原来的下一个控制点，距离点取原来控制点的和新的的一半
     * 下个控制点为新的点
     *
     * @param x 新的点的坐标
     * @param y 新的点的坐标
     */
    public void addNode(float x, float y, float width) {
        sourcePoint.set(desPoint);
        controllerPoint.set(nextPoint);
        desPoint.set(getMid(nextPoint.getX(), x), getMid(nextPoint.getY(), y), getMid(nextPoint.getWidth(), width));
        nextPoint.set(x, y, width);
    }

    public void end() {
        sourcePoint.set(desPoint);
        float x = getMid(nextPoint.getX(), sourcePoint.getX());
        float y = getMid(nextPoint.getY(), sourcePoint.getY());
        float w = getMid(nextPoint.getWidth(), sourcePoint.getWidth());
        controllerPoint.set(x, y, w);
        desPoint.set(nextPoint);
    }

    public ControllerPoint getPoint(double t) {
        float x = (float) getX(t);
        float y = (float) getY(t);
        float w = (float) getW(t);
        ControllerPoint point = new ControllerPoint();
        point.set(x, y, w);
        return point;
    }

    /**
     * 三阶曲线的控制点
     */
    private double getValue(double p0, double p1, double p2, double t) {
        double A = p2 - 2 * p1 + p0;
        double B = 2 * (p1 - p0);
        double C = p0;
        return A * t * t + B * t + C;
    }

    private double getX(double t) {
        return getValue(sourcePoint.getX(), controllerPoint.getX(), desPoint.getX(), t);
    }

    private double getY(double t) {
        return getValue(sourcePoint.getY(), controllerPoint.getY(), desPoint.getY(), t);
    }

    private double getW(double t) {
        return getWidth(sourcePoint.getWidth(), desPoint.getWidth(), t);
    }

    /**
     * @param x1 一个点的x
     * @param x2 一个点的x
     */
    private float getMid(float x1, float x2) {
        return (float) ((x1 + x2) / 2.0);
    }

    private double getWidth(double w0, double w1, double t) {
        return w0 + (w1 - w0) * t;
    }

}
