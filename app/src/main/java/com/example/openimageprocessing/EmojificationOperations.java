package com.example.openimageprocessing;

import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;
import org.opencv.core.Rect;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.Core;
import org.opencv.core.Scalar;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Canvas;
import android.graphics.Paint; 
import android.graphics.Color; 
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;

import androidx.fragment.app.FragmentActivity;
import android.app.Activity; 
import androidx.core.content.ContextCompat;


public class EmojificationOperations extends Operations{
    
    private final HashMap<String, Integer> emotionVectorMapping = new HashMap<String, Integer>() {{
        put("Neutral", R.drawable.neutral_emoji_vector);
        put("Happy", R.drawable.happy_emoji_vector);
        put("Surprise", R.drawable.surprise_emoji_vector);
        put("Sad", R.drawable.sad_emoji_vector); 
        put("Angry", R.drawable.angry_emoji_vector);
        put("Disgusted", R.drawable.disgusted_emoji_vector);
        put("Afraid", R.drawable.afraid_emoji_vector);
        put("Contempt", R.drawable.contempt_emoji_vector);
    }};

    private final String emotions[] = {
        "Neutral",
        "Happy",
        "Surprise",
        "Sad",
        "Angry",
        "Disgusted",
        "Afraid",
        "Contempt"
    };

    FragmentActivity activity;

    Paint p;

    Net emojifyNet;
    CascadeClassifier faceDetectionHaarCascadeClassifier;
    EmojificationOperations(Net emojifyNet, CascadeClassifier faceDetectionHaarCascadeClassifier, FragmentActivity activity){
        super(activity);
        this.activity = activity;
        this.emojifyNet = emojifyNet;
        this.faceDetectionHaarCascadeClassifier = faceDetectionHaarCascadeClassifier;
        p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);
        p.setAntiAlias(true);
    }

    public void emojify(){
        loadImageInMatForProcessing();
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.equalizeHist(src, src);
        MatOfRect faces = new MatOfRect();
        faceDetectionHaarCascadeClassifier.detectMultiScale(src, faces);
        List<Rect> listOfFaces = faces.toList();
        for(Rect face : listOfFaces){
            // We get the region of interest from our actual image by sampling the face
            Mat faceROI = src.submat(face);

            // In order to forward an image through the neural network, it must first be converted to a blob
            Mat blob = Dnn.blobFromImage(faceROI, 1.0, new Size(64, 64));
            emojifyNet.setInput(blob);
            Mat detections = emojifyNet.forward();
            
            // Softmax computation is essential to obtain the actual probabilities of each emotion
            // softmax calculated as, s = e^(val)/summation(e^(val))
            Mat softmax = new Mat();
            Core.exp(detections, softmax);
            Scalar sumExp = Core.sumElems(softmax);
            Scalar inverseSumExp = new Scalar(1/sumExp.val[0]);
            Core.multiply(softmax, inverseSumExp, softmax);

            // We need to get the index where the maximum value is obtained 
            Core.MinMaxLocResult res = Core.minMaxLoc(softmax);
            // create a canvas from our bitmap for drawing purposes
            Drawable emojiDrawable = ContextCompat.getDrawable(activity, emotionVectorMapping.get(emotions[(int)res.maxLoc.x]));
            Bitmap emojiBitmap = Bitmap.createBitmap(emojiDrawable.getIntrinsicWidth(), emojiDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(imageLoader);
            emojiDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            emojiDrawable.draw(canvas);
            activity.runOnUiThread(new Runnable() {
                @Override
                    public void run() {
                        loadBitmapInImageAfterProcessing();
                    }
                });
             
        }

    }
}