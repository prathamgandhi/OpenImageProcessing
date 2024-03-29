package com.example.openimageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

        int mat1magBottomAdd = mat1mag.rows() > mat2phase.rows() ? 0 : mat2phase.rows() - mat1mag.rows();
        int mat1magRightAdd = mat1mag.cols() > mat2phase.cols() ? 0 : mat2phase.cols() - mat1mag.cols();
        int mat2phaseBottomAdd = mat2phase.rows() > mat1mag.rows() ? 0 : mat1mag.rows() - mat2phase.rows();
        int mat2phaseRightAdd = mat2phase.cols() > mat1mag.cols() ? 0 : mat1mag.cols() - mat2phase.cols();
        int mat2magBottomAdd = mat2mag.rows() > mat1phase.rows() ? 0 : mat1phase.rows() - mat2mag.rows();
        int mat2magRightAdd = mat2mag.cols() > mat1phase.cols() ? 0 : mat1phase.cols() - mat2mag.cols();
        int mat1phaseBottomAdd = mat1phase.rows() > mat2mag.rows() ? 0 : mat2mag.rows() - mat1phase.rows();
        int mat1phaseRightAdd = mat1phase.cols() > mat2mag.cols() ? 0 : mat2mag.cols() - mat1phase.cols(); 

        Core.copyMakeBorder(mat1mag, mat1mag, 0, mat1magBottomAdd, 0, mat1magRightAdd, Core.BORDER_CONSTANT, Scalar.all(0));
        Core.copyMakeBorder(mat2phase, mat2phase, 0, mat2phaseBottomAdd, 0,  mat2phaseRightAdd, Core.BORDER_CONSTANT, Scalar.all(0));
        Core.copyMakeBorder(mat2mag, mat2mag, 0, mat2magBottomAdd, 0, mat2magRightAdd, Core.BORDER_CONSTANT, Scalar.all(0));
        Core.copyMakeBorder(mat1phase, mat1phase, 0, mat1phaseBottomAdd, 0, mat1phaseRightAdd, Core.BORDER_CONSTANT, Scalar.all(0));

        System.out.println(mat1mag.size());
        System.out.println(mat1phase.size());
        System.out.println(mat2mag.size());
        System.out.println(mat2phase.size());

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
        dst = new Mat();
        List<Mat> planes = new ArrayList<Mat> ();
        Core.split(newComplex1, planes);
        Core.normalize(planes.get(0), dst, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2BGRA);
        imageLoader = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
        System.out.println(dst.size() + " " + dst.channels());
        loadMatInImageAfterProcessing();
    }
}