package com.mydomain.forgery_detection;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OpenCVTest implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("OpenCV Test Matrix:\n" + mat.dump());
    }
}




