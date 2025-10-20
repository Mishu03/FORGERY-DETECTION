package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Mat;
import org.springframework.stereotype.Component;

@Component
public class PressurePatternAnalyzer {

    // Extract average pixel intensity as pressure
    public double[] extractPressureData(Mat img) {
        int rows = img.rows();
        int cols = img.cols();
        double[] data = new double[rows];
        
        for (int r = 0; r < rows; r++) {
            double sumRow = 0;
            for (int c = 0; c < cols; c++) {
                sumRow += img.get(r, c)[0];
            }
            data[r] = sumRow / cols;
        }
        return data;
    }

    // Aggregate "pressure" as average row intensity
    public double analyze(double[] pressureData) {
        double sum = 0;
        for (double d : pressureData) {
            sum += d;
        }
        return sum / pressureData.length;
    }
}
