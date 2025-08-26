package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.config.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

@Service
public class ForgeryDetectionService {

    public void processImage(String imagePath) {
        // Ensure OpenCV is loaded
        OpenCVLoader.load();

        // Load image from given path
        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            System.err.println("❌ Failed to load image at: " + imagePath);
            return;
        }

        // Print basic info about the image
        System.out.println("✅ Successfully loaded image.");
        System.out.println("   OpenCV version: " + org.opencv.core.Core.VERSION);
        System.out.println("   Image size: " + image.cols() + " x " + image.rows());
    }
}
