package com.example.openimageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;

import java.util.List;
import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;

public class FourierTransformOperations extends Operations implements FourierDialogFragment.FourierListener{

    public FourierTransformOperations(FragmentActivity activity){
        super(activity);
    }

    @Override
    public void performMagnitudePhaseMerge(Mat mat1mag, Mat mat1phase, Mat mat2){
        Utility.MagnitudePhasePair MPP2 = Utility.getFourierMagnitudePhase(mat2);
        Mat mat2mag = MPP2.mag;
        Mat mat2phase = MPP2.phase;
        Mat newMat1Re = new Mat();
        Mat newMat1Im = new Mat();
        Mat newMat2Re = new Mat();
        Mat newMat2Im = new Mat();
        // Core.copyMakeBorder(src, dst, top, left)
        Core.copyMakeBorder(mat1mag, mat1mag, 0, mat1mag.rows() + Math.max(mat1mag.rows(), mat2phase.rows()) - Math.min(mat1mag.rows(), mat2phase.rows()), 0,
                                mat1mag.cols() + Math.max(mat1mag.cols(), mat2phase.cols()) - Math.min(mat1mag.cols(), mat2phase.cols()), Core.BORDER_CONSTANT, Scalar.all(0));
        Core.copyMakeBorder(mat2phase, mat2phase, 0, mat2phase.rows() + Math.max(mat1mag.rows(), mat2phase.rows()) - Math.min(mat1mag.rows(), mat2phase.rows()), 0,
                                mat2phase.cols() + Math.max(mat1mag.cols(), mat2phase.cols()) - Math.min(mat1mag.cols(), mat2phase.cols()), Core.BORDER_CONSTANT, Scalar.all(0));
        Core.copyMakeBorder(mat2mag, mat2mag, 0, mat2mag.rows() + Math.max(mat2mag.rows(), mat1phase.rows()) - Math.min(mat2mag.rows(), mat1phase.rows()), 0,
                                mat2mag.cols() + Math.max(mat2mag.cols(), mat1phase.cols()) - Math.min(mat2mag.cols(), mat1phase.cols()), Core.BORDER_CONSTANT, Scalar.all(0));
        Core.copyMakeBorder(mat1phase, mat1phase, 0, mat1phase.rows() + Math.max(mat2mag.rows(), mat1phase.rows()) - Math.min(mat2mag.rows(), mat1phase.rows()), 0,
                                mat1phase.cols() + Math.max(mat2mag.cols(), mat1phase.cols()) - Math.min(mat2mag.cols(), mat1phase.cols()), Core.BORDER_CONSTANT, Scalar.all(0));

        Core.polarToCart(mat1mag, mat2phase, newMat1Re, newMat1Im);
        Core.polarToCart(mat2mag, mat1phase, newMat2Re, newMat2Im);
        List<Mat> planes1 = new ArrayList<Mat> ();
        List<Mat> planes2 = new ArrayList<Mat> ();
        planes1.add(newMat1Re);
        planes1.add(newMat1Im);
        planes2.add(newMat2Re);
        planes2.add(newMat2Im);
        Mat newComplex1 = new Mat();
        Core.merge(planes1, newComplex1);
        Core.idft(newComplex1, newComplex1);
        Mat restoredImage = new Mat();
        List<Mat> planes = new ArrayList<Mat> ();
        Core.split(newComplex1, planes);
        Core.normalize(planes.get(0), restoredImage, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        loadMatInImageAfterProcessing();
    }
}