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

public class SmoothingPickerDialogFragment extends DialogFragment {

    private static final String TAG = "SmoothingPickDialogFrag";

    public static final int SMOOTHENING = 1;
    public static final int SHARPENING = 2;

    Spinner dropdown;

    int operation;

    public interface SmoothingListener{
        void performNormalizedBoxFilter(int kernelSize);
        void performSquareBoxFilter(int kernelSize, boolean normalize);
        void performGaussianFilter(int kernelSize, double sigma);
        void performMedianFilter(int kernelSize);
    }

    public SmoothingListener smoothingListener;

    SmoothingPickerDialogFragment(int operation){
        this.operation = operation;
    }



    @Override
    public void onResume() {
        super.onResume();

        // Without these 2 lines the soft keyboard won't popup.
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View smootheningSharpeningDialogView = inflater.inflate(R.layout.smoothening_sharpening_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(smootheningSharpeningDialogView);


        Dialog dialog = builder.create();


        dialog.setOnShowListener(dialogInterface -> {
            dropdown = smootheningSharpeningDialogView.findViewById(R.id.spinner);
            if(operation == SMOOTHENING){
                // The various actions specified in the spinner have been listed in the strings.xml resource file.
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.smoothening_operations_array, android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);
                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        if(adapterView.getItemAtPosition(pos) == getString(R.string.normalized_box_filter)){
                            LinearLayout ll = smootheningSharpeningDialogView.findViewById(R.id.dataFetchLinearLayout);
                            // remove all views to clear all previously generated views.
                            ll.removeAllViews();
                            EditText et = new EditText(getActivity());
                            TextView tv = new TextView(getActivity());
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            et.setLayoutParams(p);
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            tv.setText("Enter Kernel Size : ");
                            ll.addView(tv);
                            ll.addView(et);

                            Button goButton = smootheningSharpeningDialogView.findViewById(R.id.ssOperation);
                            goButton.setOnClickListener(view1 -> {
                                int kernelSize = Integer.parseInt(et.getText().toString());
                                smoothingListener.performNormalizedBoxFilter(kernelSize);
                                getDialog().dismiss();
                            });
                        }
                        else if(adapterView.getItemAtPosition(pos) == getString(R.string.square_box_filter)){
                            LinearLayout ll = smootheningSharpeningDialogView.findViewById(R.id.dataFetchLinearLayout);
                            ll.removeAllViews();
                            EditText et = new EditText(getActivity());
                            TextView tvKernel = new TextView(getActivity());
                            TextView tvNormalize = new TextView(getActivity());
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            et.setLayoutParams(p);
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            tvKernel.setText("Enter Kernel Size : ");
                            tvNormalize.setText("Flip to normalize");
                            Switch sswitch = new Switch(getActivity());

                            ll.addView(tvKernel);
                            ll.addView(et);
                            ll.addView(tvNormalize);
                            ll.addView(sswitch);

                            Button goButton = smootheningSharpeningDialogView.findViewById(R.id.ssOperation);
                            goButton.setOnClickListener(view1 -> {
                                int kernelSize = Integer.parseInt(et.getText().toString());
                                smoothingListener.performSquareBoxFilter(kernelSize, sswitch.isChecked());
                                getDialog().dismiss();
                            });
                        }
                        else if(adapterView.getItemAtPosition(pos) == getString(R.string.gaussian_filter)){
                            LinearLayout ll = smootheningSharpeningDialogView.findViewById(R.id.dataFetchLinearLayout);
                            ll.removeAllViews();
                            EditText kernelSizeEditText = new EditText(getActivity());
                            EditText sigmaEditText = new EditText(getActivity());
                            TextView kernelSizeTextView = new TextView(getActivity());
                            TextView sigmaTextView = new TextView(getActivity());
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            kernelSizeEditText.setLayoutParams(p);
                            kernelSizeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            sigmaEditText.setLayoutParams(p);
                            sigmaEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            kernelSizeTextView.setText("Enter Kernel Size : ");
                            sigmaTextView.setText("Enter Sigma Value : ");

                            ll.addView(kernelSizeTextView);
                            ll.addView(kernelSizeEditText);
                            ll.addView(sigmaTextView);
                            ll.addView(sigmaEditText);

                            Button goButton = smootheningSharpeningDialogView.findViewById(R.id.ssOperation);
                            goButton.setOnClickListener(view1 -> {
                                int kernelSize = Integer.parseInt(kernelSizeEditText.getText().toString());
                                double sigma = Double.parseDouble(sigmaEditText.getText().toString());
                                smoothingListener.performGaussianFilter(kernelSize, sigma);
                                getDialog().dismiss();
                            });

                        }
                        else if(adapterView.getItemAtPosition(pos) == getString(R.string.median_filter)){
                            LinearLayout ll = smootheningSharpeningDialogView.findViewById(R.id.dataFetchLinearLayout);

                            ll.removeAllViews();
                            EditText et = new EditText(getActivity());
                            TextView tv = new TextView(getActivity());
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            et.setLayoutParams(p);
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            tv.setText("Enter Kernel Size : ");
                            ll.addView(tv);
                            ll.addView(et);

                            Button goButton = smootheningSharpeningDialogView.findViewById(R.id.ssOperation);
                            goButton.setOnClickListener(view1 -> {
                                int kernelSize = Integer.parseInt(et.getText().toString());
                                if(kernelSize % 2 == 0){
                                    Toast.makeText(getActivity(),"KernelSize should be odd",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    System.out.println(kernelSize);
                                    smoothingListener.performMedianFilter(kernelSize);
                                    getDialog().dismiss();
                                }
                            });
                        }
                        else{

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        
                    }
                });
            }
            if(operation == SHARPENING){

            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            smoothingListener = new SmoothingSharpeningOperations(getActivity());
        }
        catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: "
                    + e.getMessage());
        }
    }

}
