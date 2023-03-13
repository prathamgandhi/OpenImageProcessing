package com.example.openimageprocessing;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.Objects;

public class ImageEditorActivity extends AppCompatActivity implements KernelPickerDialogFragment.OnInputListener, SmoothingPickerDialogFragment.SmoothingListener{

    ImageView imageEditorView;
    ImageButton backButton;
    Button convolutionButton, correlationButton, smootheningButton, sharpeningButton;

    @Override
    public void performNormalizedBoxFilter(int kernelSize) {
        Bitmap bmp32 = ((BitmapDrawable)imageEditorView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Mat src = new Mat();
        Utils.bitmapToMat(bmp32, src);
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.blur(src, dst, new Size(kernelSize, kernelSize));
        Utils.matToBitmap(dst, bmp32);
        imageEditorView.setImageBitmap(bmp32);
    }

    @Override
    public void performSquareBoxFilter(int kernelSize, boolean normalize) {
        Bitmap bmp32 = ((BitmapDrawable)imageEditorView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Mat src = new Mat();
        Utils.bitmapToMat(bmp32, src);
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.sqrBoxFilter(src, dst, CvType.CV_8UC3, new Size(kernelSize, kernelSize), new Point(-1, -1), normalize);
//        Imgproc.sqrBoxFilter(src, dst, -1, new Size(kernelSize, kernelSize), new Point(-1, -1), normalize);
        System.out.println(dst.channels() + " " + dst.elemSize());
        Utils.matToBitmap(dst, bmp32);
        imageEditorView.setImageBitmap(bmp32);
    }

    @Override
    public void sendConvolutionInput(Mat kernel) {
        Core.flip(kernel, kernel, -1);
        Bitmap bmp32 = ((BitmapDrawable)imageEditorView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Mat src = new Mat();
        Utils.bitmapToMat(bmp32, src);
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.filter2D(src, dst, CvType.CV_8U, kernel);
        Utils.matToBitmap(dst, bmp32);
        imageEditorView.setImageBitmap(bmp32);
    }

    @Override
    public void sendCorrelationInput(Mat kernel) {
        Bitmap bmp32 = ((BitmapDrawable)imageEditorView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Mat src = new Mat();
        Utils.bitmapToMat(bmp32, src);
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.filter2D(src, dst, CvType.CV_8U, kernel);
        Utils.matToBitmap(dst, bmp32);
        imageEditorView.setImageBitmap(bmp32);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        imageEditorView = findViewById(R.id.imageEditorView);
        backButton = findViewById(R.id.backButton);
        convolutionButton = findViewById(R.id.convolutionButton);
        correlationButton = findViewById(R.id.correlationButton);
        smootheningButton = findViewById(R.id.smootheningButton);

        Intent intent = getIntent();
        Uri selectedImageUri = Uri.parse(intent.getStringExtra("image-uri"));
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
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

    }


}