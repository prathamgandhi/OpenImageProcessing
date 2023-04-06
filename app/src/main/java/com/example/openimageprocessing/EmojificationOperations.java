package com.example.openimageprocessing;

import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;

import org.opencv.objdetect.CascadeClassifier;

import androidx.fragment.app.FragmentActivity;

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

    Net emojifyNet;

    EmojificationOperations(Net emojifyNet, FragmentActivity activity){
        super(activity);
        this.emojifyNet = emojifyNet;
    }

    public void emojify(){
        loadImageInMatForProcessing();

        
    }
}