package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Component
public class NoiseAnalyzer {
    public double analyze(Mat img) {
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        // Apply Gaussian blur
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(3, 3), 0);
        // Difference = noise estimate
        Mat noise = new Mat();
        Core.absdiff(gray, blurred, noise);
        // Compute standard deviation
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(noise, mean, stddev);
        double noiseLevel = stddev.get(0,0)[0] / 255.0;  // normalized [0-1]
        // return value closer to 1 means high noise (more suspicious)
        return Math.min(1.0, noiseLevel);
    }
}
