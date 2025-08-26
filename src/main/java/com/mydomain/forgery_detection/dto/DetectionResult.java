package com.mydomain.forgery_detection.dto;

public abstract class DetectionResult {
    private String fileName;
    private String errorMessage;
    private double similarityScore;
    private boolean likelyGenuine;
    private long processingTimeMs;

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }
    public boolean isLikelyGenuine() { return likelyGenuine; }
    public void setLikelyGenuine(boolean likelyGenuine) { this.likelyGenuine = likelyGenuine; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}
