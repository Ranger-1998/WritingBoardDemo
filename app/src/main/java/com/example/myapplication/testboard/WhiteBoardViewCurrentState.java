package com.example.myapplication.testboard;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shutup on 16/7/11.
 */
public class WhiteBoardViewCurrentState {
    private static WhiteBoardViewCurrentState instance = null;


    private int boardColor = 0;
    private int penColor = 0;
    private int penSize = 10;
    private Paint.Cap penCap;

    private boolean isShow = true;
    private List<PathSegment> savePaths = new ArrayList<>();
    private List<PathSegment> deletePaths = new ArrayList<>();


    public List<PathSegment> getSavePaths() {
        return savePaths;
    }

    public void setSavePaths(List<PathSegment> savePaths) {
        this.savePaths = savePaths;
    }

    public List<PathSegment> getDeletePaths() {
        return deletePaths;
    }

    public void setDeletePaths(List<PathSegment> deletePaths) {
        this.deletePaths = deletePaths;
    }


    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public Paint.Cap getPenCap() {
        return penCap;
    }

    public void setPenCap(Paint.Cap penCap) {
        this.penCap = penCap;
    }

    public int getPenSize() {
        return penSize;
    }

    public void setPenSize(int penSize) {
        this.penSize = penSize;
    }

    public int getPenColor() {
        return penColor;
    }

    public void setPenColor(int penColor) {
        this.penColor = penColor;
    }

    public int getBoardColor() {
        return boardColor;
    }

    public void setBoardColor(int boardColor) {
        this.boardColor = boardColor;
    }

    public static WhiteBoardViewCurrentState getInstance(){
        if (instance == null){
            instance = new WhiteBoardViewCurrentState();
        }
        return instance;
    }



    private WhiteBoardViewCurrentState() {
        boardColor =  Color.WHITE;
        penColor = Color.BLACK;
        penSize = 10;
        penCap = Paint.Cap.ROUND;
    }
}
