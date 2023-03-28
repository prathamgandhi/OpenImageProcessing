package com.example.openimageprocessing;

import org.opencv.core.Mat;

import java.util.LinkedList;
import java.util.Stack;


public class UndoRedoStack {

    private static final int undoStackSize = 6;
    private LinkedList<Mat> ll;
    private Stack<Mat> redoStack;

    public UndoRedoStack(){
        ll = new LinkedList<>();
        redoStack = new Stack<>();
    }

    public void newOperation(Mat mat){
        if(ll.size() == 6){
            ll.removeFirst();
        }
        ll.add(mat);
        redoStack.clear();
        System.out.println(ll.size());
    }

    public Mat undo(){
        if(ll.size() == 1) return null;
        System.out.println("Current stack size : " + ll.size());
        Mat m = ll.pollLast();
        redoStack.push(m);
        System.out.println("Current stack size after undo : " + ll.size());
        return ll.getLast();
    }

    public Mat redo(){
        if(redoStack.empty()) return null;
        System.out.println("Current stack size: " + ll.size());
        Mat m = redoStack.pop();
        ll.add(m);
        System.out.println("Current stack size after redo: " + ll.size());
        return m;
    }


}
