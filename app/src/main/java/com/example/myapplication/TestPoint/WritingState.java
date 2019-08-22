package com.example.myapplication.TestPoint;


import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存笔迹信息的单例
 */
public class WritingState {
    private static volatile WritingState instance = null;

    private int penColor;
    private int penSize;

    private List<WritingStep> steps = new ArrayList<>();
    private List<WritingStep> deleteSteps = new ArrayList<>();
    private List<WritingStep> selectedSteps = new ArrayList<>();
    private WritingState(){
        penColor = Color.BLACK;
        penSize = 20;
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

    public void setSteps(List<WritingStep> steps) {
        this.steps = steps;
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
        for (WritingStep step: steps) {
            step.updatePoints(40);
        }
    }
}
