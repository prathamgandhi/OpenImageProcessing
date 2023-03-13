package com.example.openimageprocessing;

import org.opencv.core.Mat;

import java.util.LinkedList;
import java.util.Stack;

public class UndoRedoStack {

    private static final int undoStackSize = 5;
    private LinkedList<Mat> ll;
    private Stack<Mat> redoStack;

    public UndoRedoStack(){
        ll = new LinkedList<>();
        redoStack = new Stack<>();
    }

    public void newOperation(Mat mat){
        if(ll.size() == 5){
            ll.removeFirst();
        }
        ll.add(mat);
        redoStack.clear();
    }

    public Mat undo(){
        Mat m = ll.pollLast();
        redoStack.push(m);
        return m;
    }

    public Mat redo(){
        return redoStack.pop();
    }


}
