package com.example.openimageprocessing;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.opencv.core.CvType;
import org.opencv.core.Mat;


// The same dialog fragment is responsible for generation of both convolution and correlation dialogs
public class KernelPickerDialogFragment extends DialogFragment {

    private static final String TAG = "KernelPickerDialogFrag";
    public static final int CONVOLUTION = 1;
    public static final int CORRELATION = 0;

    int operation;

    Button generateGridButton, convolveButton;
    EditText kernelSizeEditText, scalarEditText;
    TableLayout kernelTable;

    EditText[][] sharableKernelCells;

    KernelPickerDialogFragment(int operation){
        this.operation = operation;
    }


    public interface OnInputListener{
        void sendConvolutionInput(Mat kernel);
        void sendCorrelationInput(Mat kernel);
    }

    public OnInputListener matOnInputListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View convolutionPopupView = inflater.inflate(R.layout.convolution_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(convolutionPopupView);


        Dialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            generateGridButton = convolutionPopupView.findViewById(R.id.generateGridButton);
            convolveButton = convolutionPopupView.findViewById(R.id.convolveButton);
            kernelSizeEditText = convolutionPopupView.findViewById(R.id.kernelSizeEditText);
            scalarEditText = convolutionPopupView.findViewById(R.id.scalarEditText);
            kernelTable = convolutionPopupView.findViewById(R.id.kernelTable);

            scalarEditText.setText("1");

            generateGridButton.setOnClickListener(view1 -> {
                kernelTable.removeAllViews();
                try {
                    int dim = Integer.parseInt(kernelSizeEditText.getText().toString().trim());
                    if (dim > 15) {
                        Toast.makeText(getActivity(), "Cannot generate such a large kernel", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TableRow rows[] = new TableRow[dim];
                    EditText kernelCells[][] = new EditText[dim][dim];
                    int ids = 0;
                    for (int i = 0; i < dim; i++) {
                        rows[i] = new TableRow(getActivity());
                        for (int j = 0; j < dim; j++) {
                            kernelCells[i][j] = new EditText(getActivity());
                            rows[i].addView(kernelCells[i][j]);
                        }
                        kernelTable.addView(rows[i]);
                    }
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    int margin = Utility.convertPixelsToDp(40, getActivity());
                    lp.setMargins(margin, margin, margin, margin);
                    // Got the pixel values by just playing around with values
                    int cellPadding = Utility.convertPixelsToDp(30, getActivity());
                    int cellWidth = Utility.convertPixelsToDp(300, getActivity());

                    for(int i = 0; i < dim; i++){
                        for(int j = 0; j < dim; j++){
                            kernelCells[i][j].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            kernelCells[i][j].setGravity(Gravity.CENTER);
                            kernelCells[i][j].setWidth(cellWidth);
                            kernelCells[i][j].setHeight(cellWidth);
                            kernelCells[i][j].setPadding(cellPadding, cellPadding, cellPadding, cellPadding);
                            kernelCells[i][j].setLayoutParams(lp);
                            kernelCells[i][j].setBackgroundResource(R.drawable.boxed_edit_text);
                            kernelCells[i][j].setText("0");
                        }
                        rows[i].setGravity(Gravity.CENTER);
                    }
                    setSharableKernelCells(kernelCells);
                }
                catch(NumberFormatException nfe){
                    Toast.makeText(getActivity(), "Enter a valid number", Toast.LENGTH_SHORT).show();
                }
            });

            convolveButton.setOnClickListener(view -> {
                EditText kernelCells[][] = getSharableKernelCells();
                int dim = kernelCells.length;
                Mat mat = new Mat(dim, dim, CvType.CV_64F);
                for(int i = 0; i < dim; i++){
                    for(int j = 0; j < dim; j++){
                        mat.put(i, j, Float.parseFloat(kernelCells[i][j].getText().toString())*Float.parseFloat(scalarEditText.getText().toString()));
                    }
                }
                if(operation == CONVOLUTION){
                    matOnInputListener.sendConvolutionInput(mat);
                }
                else if(operation == CORRELATION){
                    matOnInputListener.sendCorrelationInput(mat);
                }
                getDialog().dismiss();
            });
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            matOnInputListener = (OnInputListener) getActivity();
        }
        catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: "
                    + e.getMessage());
        }
    }

    private void setSharableKernelCells(EditText[][] kernelCells){
        this.sharableKernelCells = kernelCells;
    }

    private EditText[][] getSharableKernelCells(){
        return this.sharableKernelCells;
    }
}
