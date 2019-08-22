package com.example.myapplication.penNew;


import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 保存笔迹信息的单例
 */
public class WritingState {
    private static volatile WritingState instance = null;

    private int penColor;
    private int penSize;

    private final List<WritingStep> steps = Collections.synchronizedList(new ArrayList<>());
    private List<WritingStep> deleteSteps = new ArrayList<>();
    private List<WritingStep> selectedSteps = new ArrayList<>();
    private List<BitmapStep> bitmapSteps = new ArrayList<>();
    private List<BitmapStep> selectBitmap = new ArrayList<>();
    private WritingState(){
        penColor = Color.BLACK;
        penSize = 10;
    }

    public static WritingState getInstance() {
        if (instance == null) {
            synchronized (WritingState.class) {
                if (instance == null) {
                    instance = new WritingState();
                }
            }
        }
        return instance;
    }


    public int getPenColor() {
        return penColor;
    }

    public void setPenColor(int penColor) {
        this.penColor = penColor;
    }

    public int getPenSize() {
        return penSize;
    }

    public void setPenSize(int penSize) {
        this.penSize = penSize;
    }

    public List<WritingStep> getSteps() {
        return steps;
    }

    public List<WritingStep> getDeleteSteps() {
        return deleteSteps;
    }

    public void setDeleteSteps(List<WritingStep> deleteSteps) {
        this.deleteSteps = deleteSteps;
    }

    public List<WritingStep> getSelectedSteps() {
        return selectedSteps;
    }

    public void setSelectedSteps(List<WritingStep> selectedSteps) {
        this.selectedSteps = selectedSteps;
    }

    public void updateSteps() {
        synchronized (steps) {
            for (WritingStep step: steps) {
                step.updatePoints(40);
            }
        }
    }

    public List<BitmapStep> getBitmapSteps() {
        return bitmapSteps;
    }

    public void setBitmapSteps(List<BitmapStep> bitmapSteps) {
        this.bitmapSteps = bitmapSteps;
    }

    public List<BitmapStep> getSelectBitmap() {
        return selectBitmap;
    }

    public void setSelectBitmap(List<BitmapStep> selectBitmap) {
        this.selectBitmap = selectBitmap;
    }

}
