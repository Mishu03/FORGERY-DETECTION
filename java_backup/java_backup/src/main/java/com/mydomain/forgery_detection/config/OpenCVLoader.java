package com.mydomain.forgery_detection.config;

import org.opencv.core.Core;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OpenCVLoader {

    @PostConstruct
    public void init() {
        System.load("C:\\Users\\msnai\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
        System.out.println("Ã¢Å“â€¦ OpenCV loaded successfully!");
    }
}




