package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Component
public class StrokeConsistencyAnalyzer {

    /**
     * Analyze stroke lengths from signature image.
     * Higher consistency = higher score (0-1).
     */
    public double analyze(double[] strokes) {
        if (strokes == null || strokes.length == 0) return 0.0;

        double mean = 0.0;
        for (double s : strokes) mean += s;
        mean /= strokes.length;

        double variance = 0.0;
        for (double s : strokes) variance += Math.pow(s - mean, 2);
        variance /= strokes.length;

        // Robust normalization: 1 = consistent, 0 = variable
        return 1.0 / (1.0 + Math.sqrt(variance) / (mean + 1e-6));
    }

    /**
     * Extract stroke lengths from grayscale signature image.
     */
    public double[] extractStrokeLengths(Mat grayImg) {
        Mat edges = new Mat();
        Imgproc.Canny(grayImg, edges, 50, 150);

        int rows = grayImg.rows();
        int cols = grayImg.cols();
        double[] strokes = new double[rows];

        for (int i = 0; i < rows; i++) {
            int count = 0;
            for (int j = 0; j < cols; j++) {
                if (edges.get(i, j)[0] > 0) count++;
            }
            strokes[i] = count;
        }

        return strokes;
    }
}