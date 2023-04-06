package com.example.openimageprocessing;

import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;
import org.opencv.core.Rect;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.List;

import android.graphics.drawable.Drawable; 
import androidx.fragment.app.FragmentActivity;
import android.app.Activity; 
import androidx.core.content.ContextCompat;

public class EmojificationOperations extends Operations{

    String emotions[] = {
        "Neutral",
        "Happy",
        "Surprise",
        "Sad", 
        "Anger",
        "Disgust",
        "Fear",
        "Contempt"
    };

    FragmentActivity activity;

    Net emojifyNet;
    CascadeClassifier faceDetectionHaarCascadeClassifier;
    EmojificationOperations(Net emojifyNet, CascadeClassifier faceDetectionHaarCascadeClassifier, FragmentActivity activity){
        super(activity);
        this.activity = activity;
        this.emojifyNet = emojifyNet;
        this.faceDetectionHaarCascadeClassifier = faceDetectionHaarCascadeClassifier;
    }

    public void emojify(){
        loadImageInMatForProcessing();
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.equalizeHist(src, src);
        MatOfRect faces = new MatOfRect();
        faceDetectionHaarCascadeClassifier.detectMultiScale(src, faces);
        List<Rect> listOfFaces = faces.toList();
        for(Rect face : listOfFaces){
            Mat faceROI = src.submat(face);
            Mat blob = Dnn.blobFromImage(src, 1.0, new Size(64, 64));
            emojifyNet.setInput(blob);
            Mat detections = emojifyNet.forward();
            System.out.println(detections.dump());
            
            activity.runOnUiThread(new Runnable() {
                @Override
                    public void run() {
                        Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.smiley);
                    }
                });
             
        }

    }
}