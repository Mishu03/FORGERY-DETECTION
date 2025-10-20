package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Mat;
import org.springframework.stereotype.Component;

@Component
public class StrokeConsistencyAnalyzer {

    // Extract "stroke lengths" by counting non-zero pixels row-wise
    public double[] extractStrokeLengths(Mat img) {
        int rows = img.rows();
        double[] strokes = new double[rows];

        for (int r = 0; r < rows; r++) {
            int strokeCount = 0;
            for (int c = 0; c < img.cols(); c++) {
                double pixel = img.get(r, c)[0];
                if (pixel < 128) { // simple binary assumption
                    strokeCount++;
                }
            }
            strokes[r] = strokeCount;
        }
        return strokes;
    }

    // Aggregate feature: average stroke length
    public double analyze(double[] strokeData) {
        double sum = 0;
        for (double d : strokeData) {
            sum += d;
        }
        return strokeData.length > 0 ? sum / strokeData.length : 0;
    }
}
