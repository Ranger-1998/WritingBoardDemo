package com.example.myapplication.penNew;

public class MotionElement {

    private float x;
    private float y;
    private float pressure;
    //绘制的工具类型，判断是否是电磁笔。
    private int type;

    public MotionElement(float x, float y, float pressure, int type) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
