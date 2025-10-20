package com.mydomain.forgery_detection.config;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OpenCVLoader {

    @PostConstruct
    public void init() {
        // Use OpenPnP's auto-loader to extract and load the correct native library
        // This works cross-platform and avoids needing OPENCV_DLL_PATH
        OpenCV.loadShared();
        System.out.println("OpenCV loaded via OpenPnP. Version: " + Core.VERSION);
    }
}