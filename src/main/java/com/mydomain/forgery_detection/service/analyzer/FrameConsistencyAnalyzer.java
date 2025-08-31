package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Component;

@Component
public class FrameConsistencyAnalyzer {

    /**
     * Analyze video frame consistency.
     * Returns 0–1, higher means more consistent.
     * Logs frame-by-frame differences for debugging.
     */
    public double analyze(VideoCapture capture) {
        if (capture == null || !capture.isOpened()) {
            System.out.println("[FrameConsistencyAnalyzer] VideoCapture not opened.");
            return 0.0;
        }

        Mat prevFrame = new Mat();
        Mat currFrame = new Mat();
        Mat diff = new Mat();

        int frameCount = 0;
        double totalDiff = 0;

        try {
            if (!capture.read(prevFrame)) {
                System.out.println("[FrameConsistencyAnalyzer] Cannot read first frame.");
                return 0.0;
            }

            while (capture.read(currFrame)) {
                // Compute difference using all channels
                Core.absdiff(prevFrame, currFrame, diff);
                double meanDiff = (Core.mean(diff).val[0] + Core.mean(diff).val[1] + Core.mean(diff).val[2]) / 3.0;
                double normalizedDiff = meanDiff / 255.0;

                totalDiff += normalizedDiff;
                frameCount++;

                // Debug: print per-frame difference
                System.out.printf("[Frame %d] Mean diff: %.4f, Normalized: %.4f%n", frameCount, meanDiff, normalizedDiff);

                prevFrame.release();
                prevFrame = currFrame.clone();
            }

            if (frameCount == 0) return 1.0;

            double avgDiff = totalDiff / frameCount;
            double frameConsistency = Math.max(0.0, 1.0 - avgDiff);

            System.out.printf("[FrameConsistencyAnalyzer] Average normalized diff: %.4f, Frame consistency: %.4f%n",
                    avgDiff, frameConsistency);

            return frameConsistency;

        } finally {
            prevFrame.release();
            currFrame.release();
            diff.release();
        }
    }
}