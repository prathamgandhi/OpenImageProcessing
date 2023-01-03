package com.example.openimageprocessing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SmoothingPickerDialogFragment extends DialogFragment {

    public static final int SMOOTHENING = 1;
    public static final int SHARPENING = 2;

    Spinner dropdown;

    int operation;

    SmoothingPickerDialogFragment(int operation){
        this.operation = operation;
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
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.smoothening_operations_array, android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);
                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

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


}
