package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.VideoDetectionResult;
import com.mydomain.forgery_detection.service.analyzer.FrameConsistencyAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.MotionAnomaliesAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.AudioVideoSyncAnalyzer;
import org.springframework.web.multipart.MultipartFile;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class VideoDetectionService {

    private final FrameConsistencyAnalyzer frameAnalyzer;
    private final MotionAnomaliesAnalyzer motionAnalyzer;
    private final AudioVideoSyncAnalyzer avSyncAnalyzer;

    public VideoDetectionService(FrameConsistencyAnalyzer frameAnalyzer,
                                 MotionAnomaliesAnalyzer motionAnalyzer,
                                 AudioVideoSyncAnalyzer avSyncAnalyzer) {
        this.frameAnalyzer = frameAnalyzer;
        this.motionAnalyzer = motionAnalyzer;
        this.avSyncAnalyzer = avSyncAnalyzer;
    }

    public VideoDetectionResult analyzeVideo(MultipartFile file) {
        VideoDetectionResult result = new VideoDetectionResult();
        result.setFileName(file != null ? file.getOriginalFilename() : "unknown");
        result.setDetectionType("VIDEO");
        long start = System.currentTimeMillis();

        File tempVideo = null;
        VideoCapture capture = null;

        try {
            // Save temporary video file
            tempVideo = File.createTempFile("video-", ".mp4");
            if (file != null) file.transferTo(tempVideo);

            System.out.println("Temp video saved at: " + tempVideo.getAbsolutePath());

            // ----- FRAME CONSISTENCY -----
            capture = new VideoCapture(tempVideo.getAbsolutePath());
            if (!capture.isOpened()) {
                result.setErrorMessage("Failed to open video file");
                result.setForgeryProbability(0);
                result.setDecision("Unknown");
                result.setLikelyGenuine(false);
                return result;
            }

            double frameConsistency = frameAnalyzer.analyze(capture);
            System.out.println("Frame Consistency Score: " + frameConsistency);
            capture.release();

            // ----- MOTION ANOMALIES -----
            double motionAnomalies = motionAnalyzer.analyze(tempVideo);
            System.out.println("Motion Anomalies Score: " + motionAnomalies);

            // ----- AUDIO-VIDEO SYNC -----
            double avSyncScore = avSyncAnalyzer.analyze(tempVideo);
            System.out.println("Audio-Video Sync Score: " + avSyncScore);

            // ----- NORMALIZE & PROBABILITY -----
            double normalizedFrame = frameConsistency;       // 0-1, higher = better
            double normalizedMotion = 1 - motionAnomalies;   // 0-1, higher = better
            double normalizedAVSync = avSyncScore;           // 0-1, higher = better

            double forgeryProbability = 1 - ((normalizedFrame + normalizedMotion + normalizedAVSync) / 3.0);
            forgeryProbability = Math.min(1.0, Math.max(0.0, forgeryProbability));
            System.out.println("Calculated Forgery Probability: " + forgeryProbability);

            // ----- DECISION -----
            String decision = forgeryProbability < 0.3 ? "Likely Genuine" :
                              forgeryProbability < 0.7 ? "Suspicious" : "Likely Forged";
            System.out.println("Decision: " + decision);

            // ----- POPULATE RESULT -----
            result.setFrameConsistencyScore(frameConsistency);
            result.setMotionAnomaliesScore(motionAnomalies);
            result.setAudioVideoSyncScore(avSyncScore);
            result.setForgeryProbability(forgeryProbability);
            result.setDecision(decision);
            result.setLikelyGenuine(decision.equals("Likely Genuine"));

        } catch (Exception e) {
            e.printStackTrace();
            result.setErrorMessage("Unexpected error: " + e.getMessage());
            result.setForgeryProbability(0);
            result.setDecision("Unknown");
            result.setLikelyGenuine(false);
        } finally {
            if (capture != null && capture.isOpened()) capture.release();
            if (tempVideo != null && tempVideo.exists()) tempVideo.delete();
        }

        result.setProcessingTimeMs(System.currentTimeMillis() - start);
        System.out.println("Processing Time (ms): " + result.getProcessingTimeMs());
        return result;
    }
}