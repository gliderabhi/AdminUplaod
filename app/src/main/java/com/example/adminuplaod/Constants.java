package com.example.adminuplaod;


import android.annotation.SuppressLint;
import android.app.Application;

public class Constants {
    public static final String Category = "Category";
    public static final String PREFS_NAME = "CategoryData";
    public static final String URL = "URL";
    public static final String Time = "Time";
    public static final String Width = "width";
    public static final String height = "height";
    public static final String JOb_TAG = "Job";
    public static final String UID = "UID";
    public static final String Auto = "Auto";
    public static final String LastImageNo = "LastImageNo";
    public static final String uploadedImageNo = "uploadNo";
    public static String celebNo="Celebrities";
    public static String carNo="Cars";
    public static String spaceNo="Space";
    public static String natureNo="Nature";
    public static String buildingNo="Buildings";
    public static String oceanNo="Ocean";

    public static final String INTENT_EXTRA_ALBUM = "album";
    public static final String INTENT_EXTRA_IMAGES = "images";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final int DEFAULT_LIMIT = 10;
    public Constants() {
    }

     @SuppressLint("PrivateApi")
     static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
    }
}
