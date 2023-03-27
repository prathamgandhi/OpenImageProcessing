package com.example.openimageprocessing;

import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import androidx.fragment.app.FragmentActivity;

public class SmoothingSharpeningOperations extends Operations implements SmoothingPickerDialogFragment.SmoothingListener{

    public SmoothingSharpeningOperations(FragmentActivity activity){
        super(activity);
    }

    @Override
    public void performNormalizedBoxFilter(int kernelSize) {
        loadImageInMatForProcessing();    
        Imgproc.blur(src, dst, new Size(kernelSize, kernelSize));
        loadMatInImageAfterProcessing();
    }

    @Override
    public void performSquareBoxFilter(int kernelSize, boolean normalize) {
        loadImageInMatForProcessing();
        Imgproc.sqrBoxFilter(src, dst, CvType.CV_8UC3, new Size(kernelSize, kernelSize), new Point(-1, -1), normalize);
        loadMatInImageAfterProcessing();
    } 

    @Override
    public void performGaussianFilter(int kernelSize, double sigma){
        loadImageInMatForProcessing();
        Imgproc.GaussianBlur(src, dst, new Size(kernelSize, kernelSize), sigma);
        loadMatInImageAfterProcessing();
    }

    @Override
    public void performMedianFilter(int kernelSize){
        loadImageInMatForProcessing();
        Imgproc.medianBlur(src, dst, kernelSize);
        loadImageInMatForProcessing();
    }

}
