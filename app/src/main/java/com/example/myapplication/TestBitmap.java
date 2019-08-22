package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TestBitmap extends View {
    public TestBitmap(Context context) {
        this(context, null);
    }

    public TestBitmap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestBitmap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return true;
    }
}
