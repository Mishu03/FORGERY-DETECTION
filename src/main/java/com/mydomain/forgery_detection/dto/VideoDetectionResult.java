package com.mydomain.forgery_detection.dto;

public class VideoDetectionResult extends DetectionResult {
    private double frameConsistencyScore;
    private double motionAnomaliesScore;
    private double audioVideoSyncScore;

    public VideoDetectionResult() {
        // Default constructor
    }

    public VideoDetectionResult(String fileName, boolean likelyGenuine, double forgeryProbability) {
        setFileName(fileName);
        setLikelyGenuine(likelyGenuine);
        setForgeryProbability(forgeryProbability);
    }

    // Getters and setters
    public double getFrameConsistencyScore() {
        return frameConsistencyScore;
    }

    public void setFrameConsistencyScore(double frameConsistencyScore) {
        this.frameConsistencyScore = frameConsistencyScore;
    }

    public double getMotionAnomaliesScore() {
        return motionAnomaliesScore;
    }

    public void setMotionAnomaliesScore(double motionAnomaliesScore) {
        this.motionAnomaliesScore = motionAnomaliesScore;
    }

    public double getAudioVideoSyncScore() {
        return audioVideoSyncScore;
    }

    public void setAudioVideoSyncScore(double audioVideoSyncScore) {
        this.audioVideoSyncScore = audioVideoSyncScore;
    }

    // Optional: helpful for logging and debugging
    @Override
    public String toString() {
        return "VideoDetectionResult{" +
                "fileName='" + getFileName() + '\'' +
                ", frameConsistencyScore=" + frameConsistencyScore +
                ", motionAnomaliesScore=" + motionAnomaliesScore +
                ", audioVideoSyncScore=" + audioVideoSyncScore +
                ", forgeryProbability=" + getForgeryProbability() +
                ", decision='" + getDecision() + '\'' +
                ", likelyGenuine=" + isLikelyGenuine() +
                ", processingTimeMs=" + getProcessingTimeMs() +
                '}';
    }
}