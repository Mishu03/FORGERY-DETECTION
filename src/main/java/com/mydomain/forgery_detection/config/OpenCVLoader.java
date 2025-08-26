package com.mydomain.forgery_detection.config;

import org.opencv.core.Core;

public class OpenCVLoader {

    private static boolean loaded = false;

    public static void load() {
        if (!loaded) {
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Ensure OpenCV native library is loaded
                System.out.println("OpenCV version: " + Core.VERSION);
                loaded = true;
            } catch (UnsatisfiedLinkError e) {
                System.err.println("❌ OpenCV failed to load: " + e.getMessage());
                throw e; // Fail fast
            }
        }
    }
}
