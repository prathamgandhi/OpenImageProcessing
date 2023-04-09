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
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.content.Intent;
import android.provider.MediaStore;
import android.net.Uri;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import org.opencv.core.Mat;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.w3c.dom.Text;

// This same dialog framgment is responsible for both smoothing and sharpening actions

public class FourierDialogFragment extends DialogFragment {

    private static final String TAG = "FourierDialogFrag";

    ImageView imageEditorView;
    ActivityResultLauncher<Intent> mergeImagesOnResult;
    Bitmap bmp2;
    Mat mat2, mat1mag, mat1phase;

    public interface FourierListener{
        void performMagnitudePhaseMerge(Mat mat1mag, Mat mat1phase, Mat mat2);
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
        Bitmap imageLoader = ((BitmapDrawable)imageEditorView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Mat src = new Mat();
        Utils.bitmapToMat(imageLoader, src);

        Utility.MagnitudePhasePair MPP = Utility.getFourierMagnitudePhase(src);
        Mat magI = MPP.mag;
        Mat phaseI = MPP.phase;
        mat1mag = magI;
        mat1phase = phaseI;

        Mat matOfOnesMag = Mat.ones(magI.size(), magI.type());
        Core.add(matOfOnesMag, magI, magI);         // switch to logarithmic scale
        Core.log(magI, magI);

        Mat matOfOnesPhase = Mat.ones(phaseI.size(), phaseI.type());
        Core.add(matOfOnesPhase, phaseI, phaseI);         // switch to logarithmic scale
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
        cx = phaseI.cols()/2;
        cy = phaseI.rows()/2;
        q0 = new Mat(phaseI, new Rect(0, 0, cx, cy));   // Top-Left - Create a ROI per quadrant
        q1 = new Mat(phaseI, new Rect(cx, 0, cx, cy));  // Top-Right
        q2 = new Mat(phaseI, new Rect(0, cy, cx, cy));  // Bottom-Left
        q3 = new Mat(phaseI, new Rect(cx, cy, cx, cy)); // Bottom-Right
        tmp = new Mat();               // swap quadrants (Top-Left with Bottom-Right)
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
        q2.copyTo(q1);
        tmp.copyTo(q2);
        phaseI.convertTo(phaseI, CvType.CV_8UC1);
        Core.normalize(phaseI, phaseI, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

        Bitmap magnitudeLoader = Bitmap.createBitmap(magI.cols(), magI.rows(), Bitmap.Config.ARGB_8888);
        Bitmap phaseLoader = Bitmap.createBitmap(phaseI.cols(), phaseI.rows(), Bitmap.Config.ARGB_8888); 
        Utils.matToBitmap(magI, magnitudeLoader);
        Utils.matToBitmap(phaseI, phaseLoader);
        ImageView magnitudeLoaderView  = fourierTransformDialogView.findViewById(R.id.Amplitude);
        ImageView phaseLoaderView = fourierTransformDialogView.findViewById(R.id.phase);
        magnitudeLoaderView.setImageBitmap(magnitudeLoader);
        phaseLoaderView.setImageBitmap(phaseLoader);


        dialog.setOnShowListener(dialogInterface -> {
            /*
            Button mergeImageButton = fourierTransformDialogView.findViewById(R.id.merge2images);

            mergeImageButton.setOnClickListener(view1 -> {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                mergeImagesOnResult.launch(chooserIntent);

            });
            */
        });

        

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{

            mergeImagesOnResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            if(data != null && data.getData() != null){
                                Uri selectedImageUri = data.getData();
                                try {
                                    bmp2 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                                    mat2 = new Mat();
                                    Utils.bitmapToMat(bmp2.copy(Bitmap.Config.ARGB_8888, true), mat2);
                                    fourierListener.performMagnitudePhaseMerge(mat1mag, mat1phase, mat2);
                                    getDialog().dismiss();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            );

            fourierListener = new FourierTransformOperations(getActivity());

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
