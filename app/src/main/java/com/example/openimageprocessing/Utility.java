package com.example.openimageprocessing;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

// Containing all the random functions required to perform utility actions

public final class Utility {
    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int) (px / (metrics.densityDpi / 160f));
        return dp;
    }
}
