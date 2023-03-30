package com.example.openimageprocessing;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.CvType;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.graphics.drawable.BitmapDrawable;


public class Operations {
    ImageView imageEditorView;
    protected Mat src, dst;
    protected Bitmap imageLoader; 

    UndoRedoStack urStack = ImageEditorActivity.urStack;

    public Operations(FragmentActivity activity){
        imageEditorView = activity.findViewById(R.id.imageEditorView);
        this.urStack = ImageEditorActivity.urStack;
    }

    protected void loadImageInMatForProcessing(){
        // ARGB_8888 is the bitmap configuration which is compatible with OpenCV library for processing.
        imageLoader = ((BitmapDrawable)imageEditorView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        src = new Mat();
        Utils.bitmapToMat(imageLoader, src);
        dst = new Mat(src.rows(), src.cols(), src.type());
    }

    protected void loadMatInImageAfterProcessing(){
        Utils.matToBitmap(dst, imageLoader);
        urStack.newOperation(dst);
        imageEditorView.setImageBitmap(imageLoader);
    } 
}
