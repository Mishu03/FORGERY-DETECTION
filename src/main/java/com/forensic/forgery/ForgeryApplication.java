package com.forensic.forgery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.opencv.core.Core;   // ✅ Import OpenCV

@SpringBootApplication
public class ForgeryApplication {

    // ✅ Load OpenCV library when the app starts
    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("✅ OpenCV loaded successfully! Version: " + Core.VERSION);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Failed to load OpenCV: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ForgeryApplication.class, args);
    }
}