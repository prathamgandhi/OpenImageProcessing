package com.example.openimageprocessing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.opencv.core.Mat;
import org.w3c.dom.Text;

// This same dialog framgment is responsible for both smoothing and sharpening actions

public class FourierDialogFragment extends DialogFragment {

    private static final String TAG = "FourierDialogFrag";

    ImageView imageEditorView;


    public interface FourierListener{
           
    }

    public FourierListener fourierListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View fourierTransformDialogView = inflater.inflate(R.layout.fourier_transform_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(fourierTransformDialogView);


        Dialog dialog = builder.create();

        ImageView imageEditorView = getActivity().findViewById(R.id.imageEditorView);
        src = new Mat();
        Utils.bitmapToMat(imageLoader, src);
        src.convertTo(src, CvType.CV_8UC1);

        Mat padded = new Mat();                     //expand input image to optimal size
        int m = Core.getOptimalDFTSize( I.rows() );
        int n = Core.getOptimalDFTSize( I.cols() ); // on the border add zero values
        Core.copyMakeBorder(src, padded, 0, m - src.rows(), 0, n - src.cols(), Core.BORDER_CONSTANT, Scalar.all(0));

        List<Mat> planes = new ArrayList<Mat>();
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Mat complexI = new Mat();
        Core.merge(planes, complexI);         // Add to the expanded another plane with zeros
        Core.dft(complexI, complexI);         // this way the result may fit in the source matrix

        Core.split(complexI, planes);                               // planes.get(0) = Re(DFT(I)
                                                                    // planes.get(1) = Im(DFT(I))

        Mat magI = new Mat(src.rows(), src.cols(), src.type());
        Mat phaseI = new Mat(src.rows(), src.cols(), src.type());
        Core.magnitude(planes.get(0), planes.get(1), magI);// planes.get(0) = magnitude
        Core.phase(planes.get(0), planes.get(1), phaseI);

        Mat matOfOnesMag = Mat.ones(magI.size(), magI.type());
        Core.add(matOfOnesMag, magI, magI);         // switch to logarithmic scale
        Core.log(magI, magI);

        Mat matOfOnesPhase = Mat.ones(phaseI.size(), phaseI.type());
        Core.add(matOfOnes, phaseI, phaseI);         // switch to logarithmic scale
        Core.log(phaseI, phaseI);

        // For magnitude

        magI = magI.submat(new Rect(0, 0, magI.cols() & -2, magI.rows() & -2));
        // rearrange the quadrants of Fourier image  so that the origin is at the image center
        int cx = magI.cols()/2;
        int cy = magI.rows()/2;
        Mat q0 = new Mat(magI, new Rect(0, 0, cx, cy));   // Top-Left - Create a ROI per quadrant
        Mat q1 = new Mat(magI, new Rect(cx, 0, cx, cy));  // Top-Right
        Mat q2 = new Mat(magI, new Rect(0, cy, cx, cy));  // Bottom-Left
        Mat q3 = new Mat(magI, new Rect(cx, cy, cx, cy)); // Bottom-Right
        Mat tmp = new Mat();               // swap quadrants (Top-Left with Bottom-Right)
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
        q2.copyTo(q1);
        tmp.copyTo(q2);
        magI.convertTo(magI, CvType.CV_8UC1);
        Core.normalize(magI, magI, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

        // For phase

        phaseI = phaseI.submat(new Rect(0, 0, phaseI.cols() & -2, phaseI.rows() & -2));
        // rearrange the quadrants of Fourier image  so that the origin is at the image center
        int cx = phaseI.cols()/2;
        int cy = phaseI.rows()/2;
        Mat q0 = new Mat(phaseI, new Rect(0, 0, cx, cy));   // Top-Left - Create a ROI per quadrant
        Mat q1 = new Mat(phaseI, new Rect(cx, 0, cx, cy));  // Top-Right
        Mat q2 = new Mat(phaseI, new Rect(0, cy, cx, cy));  // Bottom-Left
        Mat q3 = new Mat(phaseI, new Rect(cx, cy, cx, cy)); // Bottom-Right
        Mat tmp = new Mat();               // swap quadrants (Top-Left with Bottom-Right)
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
        q2.copyTo(q1);
        tmp.copyTo(q2);
        phaseI.convertTo(phaseI, CvType.CV_8UC1);
        Core.normalize(phaseI, phaseI, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

        Bitmap magnitudeLoader, phaseLoader;
        Utils.matToBitmap(magI, magnitudeLoader);
        Utils.matToBitmap(phaseI, phaseLoader);
        ImageView magnitudeLoaderView  = fourierTransformDialogView.findViewById(R.id.Amplitude);
        ImageView phaseLoaderView = fourierTransformDialogView.findViewById(R.id.phase);
        magnitudeLoaderView.setImageBitmap(magnitudeLoader);
        phaseLoaderView.setImageBitmap(phaseLoader);

        dialog.setOnShowListener(dialogInterface -> {
            
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            // SmoothingSharpeningOperations smoothingSharpeningListener = new SmoothingSharpeningOperations(getActivity());
            // smoothingListener = smoothingSharpeningListener;
            // sharpeningListener = smoothingSharpeningListener;
        }
        catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: "
                    + e.getMessage());
        }
    }

}
