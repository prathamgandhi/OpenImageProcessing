package com.example.openimageprocessing;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;


import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

import java.util.List;
import java.util.ArrayList;

// Containing all the random functions required to perform utility actions

public final class Utility {

    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int) (px / (metrics.densityDpi / 160f));
        return dp;
    }

    public static MagnitudePhasePair getFourierMagnitudePhase(Mat src){
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Mat padded = new Mat();                     //expand input image to optimal size
        int m = Core.getOptimalDFTSize( src.rows() );
        int n = Core.getOptimalDFTSize( src.cols() ); // on the border add zero values
        Core.copyMakeBorder(src, padded, 0, m - src.rows(), 0, n - src.cols(), Core.BORDER_CONSTANT, Scalar.all(0));
        List<Mat> planes = new ArrayList<Mat>();
        padded.convertTo(padded, CvType.CV_32FC1);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32FC1));

        Mat complexI = new Mat();
        Core.merge(planes, complexI);         // Add to the expanded another plane with zeros

        Core.dft(complexI, complexI);         // this way the result may fit in the source matrix

        Core.split(complexI, planes);                               // planes.get(0) = Re(DFT(I)
                                                                    // planes.get(1) = Im(DFT(I))
        Mat magI = new Mat(src.rows(), src.cols(), src.type());
        Mat phaseI = new Mat(src.rows(), src.cols(), src.type());
        Core.magnitude(planes.get(0), planes.get(1), magI);// planes.get(0) = magnitude
        Core.phase(planes.get(0), planes.get(1), phaseI);
        MagnitudePhasePair MPP = new MagnitudePhasePair(magI, phaseI);
        return MPP;
    }

    public static class MagnitudePhasePair{
        public Mat mag = null;
        public Mat phase = null;

        public MagnitudePhasePair(Mat mag, Mat phase){
            this.mag = mag;
            this.phase = phase;
        }
    }
}


