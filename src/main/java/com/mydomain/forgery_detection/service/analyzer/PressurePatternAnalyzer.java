package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Mat;
import org.springframework.stereotype.Component;

@Component
public class PressurePatternAnalyzer {

    /**
     * Compute pressure pattern score from grayscale signature image.
     * Darker pixels = higher pressure. Normalized 0-1.
     */
    public double analyze(double[] pressureData) {
        if (pressureData == null || pressureData.length == 0) return 0.0;

        double sum = 0.0;
        for (double p : pressureData) sum += p;

        // Normalize with log-scale to reduce extreme sensitivity
        double avg = sum / pressureData.length;
        double normalized = avg / 255.0;
        return Math.min(1.0, Math.sqrt(normalized)); // sqrt to amplify smaller values
    }

    /**
     * Extract pressure data from grayscale signature image.
     */
    public double[] extractPressureData(Mat grayImg) {
        int rows = grayImg.rows();
        int cols = grayImg.cols();
        double[] pressure = new double[rows * cols];

        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double pixel = grayImg.get(i, j)[0];
                pressure[idx++] = 255 - pixel; // darker = higher pressure
            }
        }
        return pressure;
    }
}