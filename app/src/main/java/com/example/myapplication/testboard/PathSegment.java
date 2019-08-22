package com.example.myapplication.testboard;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by shutup on 16/7/11.
 */
public class PathSegment {
    public Paint getPaint() {
        return mPaint;
    }

    public Path getPath() {
        return mPath;
    }

    private Paint mPaint;
    private Path mPath;

    public PathSegment(Paint paint, Path path) {
        mPaint = paint;
        mPath = path;
    }
}
