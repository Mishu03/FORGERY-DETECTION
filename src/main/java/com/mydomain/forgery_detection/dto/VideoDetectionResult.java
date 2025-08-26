package com.mydomain.forgery_detection.dto;

public class VideoDetectionResult extends DetectionResult {
    private int totalFrames;
    private int analyzedFrames;
    private double frameConsistencyScore;
    private double audioVisualSyncScore;

    // Getters and Setters
    public int getTotalFrames() { return totalFrames; }
    public void setTotalFrames(int totalFrames) { this.totalFrames = totalFrames; }
    public int getAnalyzedFrames() { return analyzedFrames; }
    public void setAnalyzedFrames(int analyzedFrames) { this.analyzedFrames = analyzedFrames; }
    public double getFrameConsistencyScore() { return frameConsistencyScore; }
    public void setFrameConsistencyScore(double frameConsistencyScore) { this.frameConsistencyScore = frameConsistencyScore; }
    public double getAudioVisualSyncScore() { return audioVisualSyncScore; }
    public void setAudioVisualSyncScore(double audioVisualSyncScore) { this.audioVisualSyncScore = audioVisualSyncScore; }
}
