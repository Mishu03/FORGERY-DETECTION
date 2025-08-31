package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class MotionAnomaliesAnalyzer {

    private final double DIFF_THRESHOLD = 0.05; // adjustable sensitivity

    /**
     * Analyze motion anomalies in a video file.
     * Returns 0–1, higher means more anomalies detected.
     */
    public double analyze(File videoFile) {
        if (videoFile == null || !videoFile.exists()) {
            System.out.println("[Motion] Video file is null or missing");
            return 0.0;
        }

        VideoCapture capture = new VideoCapture(videoFile.getAbsolutePath());
        if (!capture.isOpened()) {
            System.out.println("[Motion] Failed to open video: " + videoFile.getAbsolutePath());
            return 0.0;
        }

        Mat prevFrame = new Mat();
        Mat currFrame = new Mat();
        Mat diff = new Mat();

        int frameCount = 0;
        int anomalyCount = 0;

        if (!capture.read(prevFrame)) {
            capture.release();
            return 0.0;
        }

        while (capture.read(currFrame)) {
            Core.absdiff(prevFrame, currFrame, diff);
            double meanDiff = Core.mean(diff).val[0] / 255.0;

            if (meanDiff > DIFF_THRESHOLD) {
                anomalyCount++;
            }

            prevFrame = currFrame.clone();
            frameCount++;
        }

        capture.release();

        double anomalyRatio = frameCount > 0 ? (double) anomalyCount / frameCount : 0.0;
        System.out.println("[Motion] Total frames: " + frameCount + ", Anomalies: " + anomalyCount + ", Ratio: " + anomalyRatio);
        return Math.min(1.0, anomalyRatio);
    }
}