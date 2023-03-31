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



    public interface FourierListener{
           
    }

    public FourierListener fourierListener;


    // @Override
    // public void onResume() {
    //     super.onResume();

    //     // Without these 2 lines the soft keyboard won't popup.
    //     getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    //     getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    // }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View fourierTransformDialogView = inflater.inflate(R.layout.fourier_transform_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(fourierTransformDialogView);


        Dialog dialog = builder.create();


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
