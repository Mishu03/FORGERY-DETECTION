package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Component
public class StructuralSimilarityAnalyzer {

    /**
     * Compute structural similarity index (SSIM) between two grayscale images.
     * Returns value between 0 and 1.
     */
    public double analyze(double[][] ref, double[][] test) {
        if (ref == null || test == null) return 0.0;

        int rows = Math.min(ref.length, test.length);
        int cols = Math.min(ref[0].length, test[0].length);

        double meanRef = 0.0, meanTest = 0.0;
        double varRef = 0.0, varTest = 0.0, covariance = 0.0;
        int n = rows * cols;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                meanRef += ref[i][j];
                meanTest += test[i][j];
            }
        }
        meanRef /= n;
        meanTest /= n;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double dr = ref[i][j] - meanRef;
                double dt = test[i][j] - meanTest;
                varRef += dr * dr;
                varTest += dt * dt;
                covariance += dr * dt;
            }
        }
        varRef /= n;
        varTest /= n;
        covariance /= n;

        double C1 = 6.5025, C2 = 58.5225;

        double ssim = ((2 * meanRef * meanTest + C1) * (2 * covariance + C2)) /
                      ((meanRef * meanRef + meanTest * meanTest + C1) * (varRef + varTest + C2));

        // Clamp
        ssim = Math.max(0.0, Math.min(1.0, ssim));

        // Smooth adjustment: amplify mid-range differences
        return Math.pow(ssim, 0.8);
    }

    public double[][] matToDoubleArray(Mat mat) {
        int rows = mat.rows();
        int cols = mat.cols();
        double[][] array = new double[rows][cols];

        Mat gray = new Mat();
        if (mat.channels() > 1) Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        else gray = mat.clone();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = gray.get(i, j)[0] / 255.0; // normalize 0-1
            }
        }
        return array;
    }
}