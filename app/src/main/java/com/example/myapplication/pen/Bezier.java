package com.example.myapplication.pen;

/**
 * @author shiming
 * @version v1.0 create at 2017/8/24
 * @des  对点的位置和宽度控制的bezier曲线，主要是两个点，都包含了宽度和点的坐标
 */
public class Bezier {
    //控制点的，
    private ControllerPoint controllerPoint = new ControllerPoint();
    //距离
    private ControllerPoint desPoint = new ControllerPoint();
    //下一个需要控制点
    private ControllerPoint nextPoint = new ControllerPoint();
    //资源的点
    private ControllerPoint sourcePoint = new ControllerPoint();

    public Bezier() {
    }

    /**
     * 初始化两个点，
     * @param last 最后的点的信息
     * @param cur 当前点的信息,当前点的信息，当前点的是根据事件获得，同时这个当前点的宽度是经过计算的得出的
     */
    public void init(ControllerPoint last, ControllerPoint cur)
    {
        init(last.x, last.y, last.width, cur.x, cur.y, cur.width);
    }

    private void init(float lastX, float lastY, float lastWidth, float x, float y, float width)
    {
        //资源点设置，最后的点的为资源点
        sourcePoint.set(lastX, lastY, lastWidth);
        float midX = getMid(lastX, x);
        float midY = getMid(lastY, y);
        float midW = getMid(lastWidth, width);
        //距离点为平均点
        desPoint.set(midX, midY, midW);
        //控制点为当前的距离点
        controllerPoint.set(getMid(lastX,midX),getMid(lastY,midY),getMid(lastWidth,midW));
        //下个控制点为当前点
        nextPoint.set(x, y, width);
    }

    public void addNode(ControllerPoint cur){
        addNode(cur.x, cur.y, cur.width);
    }

    /**
     * 替换就的点，原来的距离点变换为资源点，控制点变为原来的下一个控制点，距离点取原来控制点的和新的的一半
     * 下个控制点为新的点
     * @param x 新的点的坐标
     * @param y 新的点的坐标
     */
    public void addNode(float x, float y, float width){
        sourcePoint.set(desPoint);
        controllerPoint.set(nextPoint);
        desPoint.set(getMid(nextPoint.x, x), getMid(nextPoint.y, y), getMid(nextPoint.width, width));
        nextPoint.set(x, y, width);
    }

    /**
     * 结合手指抬起来的动作，告诉现在的曲线控制点也必须变化，其实在这里也不需要结合着up事件使用
     * 因为在down的事件中，所有点都会被重置，然后设置这个没有多少意义，但是可以改变下个事件的朝向改变
     * 先留着，因为后面如果需要控制整个颜色的改变的话，我的依靠这个方法，还有按压的时间的变化
     */
    public void end() {
        sourcePoint.set(desPoint);
        float x = getMid(nextPoint.x, sourcePoint.x);
        float y = getMid(nextPoint.y, sourcePoint.y);
        float w = getMid(nextPoint.width, sourcePoint.width);
        controllerPoint.set(x, y, w);
        desPoint.set(nextPoint);
    }

    /**
     *
     * @param t 孔子
     * @return
     */
    public ControllerPoint getPoint(double t){
        float x = (float)getX(t);
        float y = (float)getY(t);
        float w = (float)getW(t);
        ControllerPoint point = new ControllerPoint();
        point.set(x,y,w);
        return point;
    }

    /**
     * 三阶曲线的控制点
     * @param p0
     * @param p1
     * @param p2
     * @param t
     * @return
     */
    private double getValue(double p0, double p1, double p2, double t){
        double A = p2 - 2 * p1 + p0;
        double B = 2 * (p1 - p0);
        double C = p0;
        return A * t * t + B * t + C;
    }

    private double getX(double t) {
        return getValue(sourcePoint.x, controllerPoint.x, desPoint.x, t);
    }

    private double getY(double t) {
        return getValue(sourcePoint.y, controllerPoint.y, desPoint.y, t);
    }

    private double getW(double t){
        return getWidth(sourcePoint.width, desPoint.width, t);
    }

    /**
     *
     * @param x1 一个点的x
     * @param x2 一个点的x
     */
    private float getMid(float x1, float x2) {
        return (float)((x1 + x2) / 2.0);
    }

    private double getWidth(double w0, double w1, double t){
        return w0 + (w1 - w0) * t;
    }

}
