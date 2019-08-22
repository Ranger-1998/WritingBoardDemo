package com.example.myapplication.strokTest;

public class TimePoint {
    public float x;
    public float y;
    public float width;

    public TimePoint(float x, float y, float pressure) {
        this.x = x;
        this.y = y;
        this.width = 100 * pressure;
    }

    public void set(float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void set(TimePoint p) {
        this.x = p.x;
        this.y = p.y;
        this.width = p.width;
    }
}
