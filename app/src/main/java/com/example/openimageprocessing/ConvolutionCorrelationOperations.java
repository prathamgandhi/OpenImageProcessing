package com.example.openimageprocessing;

import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import androidx.fragment.app.FragmentActivity;

public class ConvolutionCorrelationOperations extends Operations implements KernelPickerDialogFragment.OnInputListener{

    public ConvolutionCorrelationOperations(FragmentActivity activity){
        super(activity);
    }    

    @Override
    public void sendConvolutionInput(Mat kernel) {
        Core.flip(kernel, kernel, -1);
        System.out.println(kernel.dump());
        loadImageInMatForProcessing();
        Imgproc.filter2D(src, dst, CvType.CV_8U, kernel);
        loadMatInImageAfterProcessing();
    }

    @Override
    public void sendCorrelationInput(Mat kernel) {
        loadImageInMatForProcessing();
        System.out.println(kernel.dump());
        Imgproc.filter2D(src, dst, CvType.CV_8U, kernel);
        loadMatInImageAfterProcessing();
    }

}
