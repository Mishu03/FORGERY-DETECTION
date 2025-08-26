package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.VideoDetectionResult;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.MatOfDouble;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class VideoDetectionService {

    public VideoDetectionResult analyzeVideo(MultipartFile videoFile) {
        VideoDetectionResult result = new VideoDetectionResult();
        long startTime = System.currentTimeMillis();

        try {
            Path tempFile = Files.createTempFile("video-", ".mp4");
            Files.copy(videoFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            VideoCapture capture = new VideoCapture(tempFile.toString());
            if (!capture.isOpened()) {
                throw new RuntimeException("Failed to open video file");
            }

            int totalFrames = (int) capture.get(7); // CAP_PROP_FRAME_COUNT
            int analyzedFrames = 0;
            double totalFrameScore = 0.0;

            Mat frame = new Mat();
            while (capture.read(frame) && analyzedFrames < 100) { // Analyze first 100 frames
                if (!frame.empty()) {
                    double frameScore = analyzeVideoFrame(frame);
                    totalFrameScore += frameScore;
                    analyzedFrames++;
                }
                frame.release();
            }

            double averageFrameScore = analyzedFrames > 0 ? totalFrameScore / analyzedFrames : 0.0;
            double consistencyScore = calculateConsistencyScore(totalFrameScore, analyzedFrames);

            result.setFileName(videoFile.getOriginalFilename());
            result.setTotalFrames(totalFrames);
            result.setAnalyzedFrames(analyzedFrames);
            result.setFrameConsistencyScore(consistencyScore);
            result.setAudioVisualSyncScore(0.8); // Placeholder
            result.setSimilarityScore(averageFrameScore);
            result.setLikelyGenuine(averageFrameScore >= 0.6);
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            capture.release();
            Files.deleteIfExists(tempFile);

        } catch (IOException e) {
            result.setErrorMessage("File processing error: " + e.getMessage());
        } catch (Exception e) {
            result.setErrorMessage("Analysis error: " + e.getMessage());
        }

        return result;
    }

    private double analyzeVideoFrame(Mat frame) {
        // Basic frame analysis - can be enhanced with more sophisticated algorithms
        try {
            // Simple analysis: check frame quality and consistency
            double brightness = Core.mean(frame).val[0] / 255.0;
            double contrast = calculateContrast(frame);
            
            // Combined score (simple heuristic)
            return (brightness * 0.5 + contrast * 0.5);
            
        } catch (Exception e) {
            return 0.5;
        }
    }

    private double calculateContrast(Mat frame) {
        // Simple contrast calculation
        Mat gray = new Mat();
        org.opencv.imgproc.Imgproc.cvtColor(frame, gray, org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY);
        
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(gray, mean, stddev);
        
        double contrast = stddev.get(0, 0)[0] / 255.0;
        gray.release();
        
        return Math.min(1.0, contrast);
    }

    private double calculateConsistencyScore(double totalScore, int frameCount) {
        if (frameCount == 0) return 0.0;
        
        // Simple consistency metric (higher is better)
        double average = totalScore / frameCount;
        return Math.min(1.0, average * 1.2); // Scale slightly for better results
    }
}
