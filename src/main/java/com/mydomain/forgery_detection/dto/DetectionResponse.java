package com.mydomain.forgery_detection.dto;

public class DetectionResponse {

    private String fileName;
    private String detectionType;   // "image" | "signature" | "video"
    private double similarityScore;
    private boolean genuine;
    private String message;
    private long processingTimeMs;

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getDetectionType() { return detectionType; }
    public void setDetectionType(String detectionType) { this.detectionType = detectionType; }
    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }
    public boolean isGenuine() { return genuine; }
    public void setGenuine(boolean genuine) { this.genuine = genuine; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}