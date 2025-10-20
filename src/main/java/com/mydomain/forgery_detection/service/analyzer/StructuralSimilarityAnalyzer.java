package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Mat;
import org.springframework.stereotype.Component;

@Component
public class StructuralSimilarityAnalyzer {

    // Convert an OpenCV Mat into a double[][] array (grayscale pixels)
    public double[][] matToDoubleArray(Mat mat) {
        int rows = mat.rows();
        int cols = mat.cols();
        double[][] array = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                array[r][c] = mat.get(r, c)[0];
            }
        }
        return array;
    }

    // Dummy similarity function (to be replaced with SSIM or similar in real-world)
    public double analyze(double[][] refArray, double[][] testArray) {
        int rows = Math.min(refArray.length, testArray.length);
        int cols = Math.min(refArray[0].length, testArray[0].length);

        double diffSum = 0;
        double total = rows * cols;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                diffSum += Math.abs(refArray[r][c] - testArray[r][c]);
            }
        }

        double normalizedDiff = diffSum / (total * 255.0);
        return 1.0 - normalizedDiff; // closer to 1 means more similar
    }
}
