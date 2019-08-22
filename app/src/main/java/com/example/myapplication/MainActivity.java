package com.example.myapplication;

import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView textView = new TextView(this);
//        textView.setText("WindowManagerTest!");
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams
//                (WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
//                        WindowManager.LayoutParams.TYPE_TOAST, 0, PixelFormat.TRANSPARENT);
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
//        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//        layoutParams.x = 100;
//        layoutParams.y = 300;
//        WindowManager manager = (WindowManager) getApplication().
//                getSystemService(getApplication().WINDOW_SERVICE);
//        manager.addView(textView, layoutParams);
        setContentView(R.layout.activity_main);

    }
}
