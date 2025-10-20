package com.mydomain.forgery_detection.dto;

public class SignatureVerificationResult {
    private String fileName;
    private String detectionType;
    private String decision;
    private double structuralSimilarity;
    private double pressurePatternScore;
    private double strokeConsistencyScore;
    private double forgeryProbability;
    private boolean likelyGenuine;
    private long processingTimeMs;
    private String errorMessage;

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDetectionType() {
        return detectionType;
    }
    public void setDetectionType(String detectionType) {
        this.detectionType = detectionType;
    }

    public String getDecision() {
        return decision;
    }
    public void setDecision(String decision) {
        this.decision = decision;
    }

    public double getStructuralSimilarity() {
        return structuralSimilarity;
    }
    public void setStructuralSimilarity(double structuralSimilarity) {
        this.structuralSimilarity = structuralSimilarity;
    }

    public double getPressurePatternScore() {
        return pressurePatternScore;
    }
    public void setPressurePatternScore(double pressurePatternScore) {
        this.pressurePatternScore = pressurePatternScore;
    }

    public double getStrokeConsistencyScore() {
        return strokeConsistencyScore;
    }
    public void setStrokeConsistencyScore(double strokeConsistencyScore) {
        this.strokeConsistencyScore = strokeConsistencyScore;
    }

    public double getForgeryProbability() {
        return forgeryProbability;
    }
    public void setForgeryProbability(double forgeryProbability) {
        this.forgeryProbability = forgeryProbability;
    }

    public boolean isLikelyGenuine() {
        return likelyGenuine;
    }
    public void setLikelyGenuine(boolean likelyGenuine) {
        this.likelyGenuine = likelyGenuine;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
