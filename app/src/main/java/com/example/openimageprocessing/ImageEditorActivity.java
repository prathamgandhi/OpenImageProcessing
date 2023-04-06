package com.example.openimageprocessing;

/*
 * This activity is the editor activity, here the user will get the ability to manipulate images using various image processing techniques.
*/


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.ContentValues;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatImageButton;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;

import java.io.IOException;
import java.util.Objects;
import java.io.OutputStream; 

public class ImageEditorActivity extends AppCompatActivity {

    ImageView imageEditorView;
    ImageButton backButton;
    Button convolutionButton, correlationButton, smootheningButton, sharpeningButton, fourierButton, emojifyButton;
    ImageButton undoButton, redoButton, saveButton;

    private final String emojifyModel = "emotion-ferplus-8.onnx";
    Net emojifyNet = null;

    public static UndoRedoStack urStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        urStack = new UndoRedoStack();

        // Used to setup the toolbar containing various options such as save, redo, undo, etc.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        imageEditorView = findViewById(R.id.imageEditorView);
        backButton = findViewById(R.id.backButton);
        convolutionButton = findViewById(R.id.convolutionButton);
        correlationButton = findViewById(R.id.correlationButton);
        smootheningButton = findViewById(R.id.smootheningButton);
        sharpeningButton = findViewById(R.id.sharpeningButton);
        fourierButton = findViewById(R.id.fourierButton);
        emojifyButton = findViewById(R.id.emojifyButton);

        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        Uri selectedImageUri = Uri.parse(intent.getStringExtra("image-uri"));
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), mat);
            urStack.newOperation(mat);
            imageEditorView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        backButton.setOnClickListener(view -> finish());

        convolutionButton.setOnClickListener(view -> {
            DialogFragment kernelPickerDialogFragment = new KernelPickerDialogFragment(KernelPickerDialogFragment.CONVOLUTION);
            kernelPickerDialogFragment.show(getSupportFragmentManager(), "KernelSelect");
        });

        correlationButton.setOnClickListener(view -> {
            DialogFragment kernelPickerDialogFragment = new KernelPickerDialogFragment(KernelPickerDialogFragment.CORRELATION);
            kernelPickerDialogFragment.show(getSupportFragmentManager(), "KernelSelect");
        });

        smootheningButton.setOnClickListener(view -> {
            DialogFragment smoothingPickerDialogFragment = new SmoothingPickerDialogFragment(SmoothingPickerDialogFragment.SMOOTHENING);
            smoothingPickerDialogFragment.show(getSupportFragmentManager(), "FilterSelect");
        });

        sharpeningButton.setOnClickListener(view -> {
            DialogFragment smoothingPickerDialogFragment = new SmoothingPickerDialogFragment(SmoothingPickerDialogFragment.SHARPENING);
            smoothingPickerDialogFragment.show(getSupportFragmentManager(), "FilterSelect");
        });

        fourierButton.setOnClickListener(view -> {
            DialogFragment fourierDialogFragment = new FourierDialogFragment();
            fourierDialogFragment.show(getSupportFragmentManager(), "Fourier");
        });

        emojifyButton.setOnClickListener(view -> {
            // here we always check if we don't already have a model, if not checked then we are doing more work always loading the model
            if(emojifyNet == null){
                String modelPath = Utility.getPath(emojifyModel, this);
                emojifyNet = Dnn.readNetFromONNX(modelPath);
            }
            
        });

        undoButton.setOnClickListener(view -> {
            Mat uMat = urStack.undo();
            if (uMat != null){
                Bitmap bmp32 = Bitmap.createBitmap(uMat.cols(), uMat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(uMat, bmp32);
                imageEditorView.setImageBitmap(bmp32);
            }
        });

        redoButton.setOnClickListener(view -> {
            Mat rMat = urStack.redo();
            if (rMat != null){
                Bitmap bmp32 = Bitmap.createBitmap(rMat.cols(), rMat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(rMat, bmp32);
                imageEditorView.setImageBitmap(bmp32);
            }
        });

        saveButton.setOnClickListener(view -> {
            Bitmap saveBmp = ((BitmapDrawable)imageEditorView.getDrawable()).getBitmap();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "any_picture_name");
            values.put(MediaStore.Images.Media.BUCKET_ID, "test");
            values.put(MediaStore.Images.Media.DESCRIPTION, "test Image taken");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            OutputStream outstream;
            try {
                outstream = getContentResolver().openOutputStream(uri);
                saveBmp.compress(Bitmap.CompressFormat.JPEG, 70, outstream);
                outstream.close();
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    

}
