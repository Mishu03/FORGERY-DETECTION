package com.mydomain.forgery_detection.dto;

public class DetectionResult {

    private String fileName;
    private String errorMessage;
    private double similarityScore;
    private boolean likelyGenuine;
    private long processingTimeMs;
    private String detectionType; // IMAGE, VIDEO, SIGNATURE
    private double forgeryProbability; // NEW
    private String decision;           // NEW

    // Constructors
    public DetectionResult() {}

    public DetectionResult(String fileName, String errorMessage, double similarityScore,
                           boolean likelyGenuine, long processingTimeMs, String detectionType,
                           double forgeryProbability, String decision) {
        this.fileName = fileName;
        this.errorMessage = errorMessage;
        this.similarityScore = similarityScore;
        this.likelyGenuine = likelyGenuine;
        this.processingTimeMs = processingTimeMs;
        this.detectionType = detectionType;
        this.forgeryProbability = forgeryProbability;
        this.decision = decision;
    }

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

    public String getDetectionType() { return detectionType; }
    public void setDetectionType(String detectionType) { this.detectionType = detectionType; }

    public double getForgeryProbability() { return forgeryProbability; }
    public void setForgeryProbability(double forgeryProbability) { this.forgeryProbability = forgeryProbability; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}




