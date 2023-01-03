package com.example.openimageprocessing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private static final String TAG = "MainActivity";
    public static final int PICK_IMAGE = 1;

    Button pickImage, performAction;
    ImageView imageDisplayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickImage = findViewById(R.id.pickImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        performAction = findViewById(R.id.performAction);
        imageDisplayView = findViewById(R.id.imageDisplayView);
        */
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                launchSameActivityOnResult.launch(chooserIntent);
            }
        });

        /*
        performAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat src = new Mat();
                Bitmap bmp32 = ((BitmapDrawable)imageDisplayView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                Utils.bitmapToMat(bmp32, src);
                Mat dst = new Mat(src.rows(), src.cols(), src.type());
                Imgproc.GaussianBlur(src, dst, new Size(27,27), 0);
                Utils.matToBitmap(dst, bmp32);
                imageDisplayView.setImageBitmap(bmp32);
            }
        });
        */

    }
    ActivityResultLauncher<Intent> launchSameActivityOnResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            Uri selectedImageUri = data.getData();
                            Intent intent = new Intent(MainActivity.this, ImageEditorActivity.class);
                            intent.putExtra("image-uri", selectedImageUri.toString());
                            startActivity(intent);
                        }
                    }
                }
            }
    );
}