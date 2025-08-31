package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Component
public class NoiseAnalyzer {

    /**
     * Returns a noise score between 0 and 1 (higher = noisier)
     */
    public double analyze(Mat image) {
        try {
            Mat gray = new Mat();
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

            Mat laplacian = new Mat();
            Imgproc.Laplacian(gray, laplacian, CvType.CV_64F);

            Mat mean = new Mat();
            Mat stddev = new Mat();
            Core.meanStdDev(laplacian, mean, stddev);

            double variance = Math.pow(stddev.get(0, 0)[0], 2);

            // Normalize: typical images variance ~0-1000
            double score = Math.min(1.0, variance / 1000.0);
            return score;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}




