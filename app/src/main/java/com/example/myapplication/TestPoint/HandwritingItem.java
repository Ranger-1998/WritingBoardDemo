package com.example.myapplication.TestPoint;

import android.graphics.Paint;
import android.graphics.Path;


public class HandwritingItem {
    private Path path;
    private Paint paint;
    private float width;

    public HandwritingItem(Path path, Paint paint, float width) {
        this.path = path;
        this.paint = paint;
        this.width = width;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
