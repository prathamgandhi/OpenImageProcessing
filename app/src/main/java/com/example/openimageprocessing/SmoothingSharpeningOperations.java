package com.example.openimageprocessing;

import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Scalar;

import androidx.fragment.app.FragmentActivity;

public class SmoothingSharpeningOperations extends Operations implements SmoothingPickerDialogFragment.SmoothingListener, SmoothingPickerDialogFragment.SharpeningListener{

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
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);
        Imgproc.medianBlur(src, dst, kernelSize);
        loadMatInImageAfterProcessing();
    }

    @Override
    public void performLaplacianFilter(int kernelSize){
        loadImageInMatForProcessing();
        Imgproc.Laplacian(src, dst, CvType.CV_64F, kernelSize);
        dst.convertTo(dst, CvType.CV_8U);
        loadMatInImageAfterProcessing();
    }

    @Override
    public void performHighBoostFilter(int kernelSize, double sigma, double k){
        loadImageInMatForProcessing();
        Mat blur = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.GaussianBlur(src, blur, new Size(kernelSize, kernelSize), sigma);
        Mat unsharpMask = new Mat(src.rows(), src.cols(), src.type());
        Core.subtract(src, blur, unsharpMask);
        Scalar s = new Scalar(k);
        Core.multiply(unsharpMask, s, unsharpMask);
        Core.add(src, unsharpMask, dst);
        loadMatInImageAfterProcessing();
    }

    @Override
    public void performUnsharpMasking(int kernelSize, double sigma){
        loadImageInMatForProcessing();
        Mat blur = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.GaussianBlur(src, blur, new Size(kernelSize, kernelSize), sigma);
        Mat unsharpMask = new Mat(src.rows(), src.cols(), src.type());
        Core.subtract(src, blur, unsharpMask);
        Core.add(src, unsharpMask, dst);
        loadMatInImageAfterProcessing();
    }

}
