package com.forensic.forgery;

import javax.annotation.PostConstruct;
import org.opencv.core.Core;
import org.springframework.stereotype.Component;

@Component
public class OpenCvConfig {

    @PostConstruct
    public void initOpenCv() {
        try {
            // Load the native OpenCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            System.out.println("✅ OpenCV loaded successfully! Version: " + Core.VERSION);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Failed to load OpenCV: " + e.getMessage());
        }
    }
}