package com.mydomain.forgery_detection.dto;

public abstract class DetectionResult {

    private String fileName;               // Name of the file being analyzed
    private String detectionType;          // Type of detection (IMAGE, SIGNATURE, VIDEO)
    private String decision;               // Decision result (Likely Genuine / Suspicious / Likely Forged / Unknown)
    private boolean likelyGenuine;         // Indicates if the result is likely genuine
    private double forgeryProbability;     // Probability of forgery
    private String errorMessage;           // Error message if any
    private long processingTimeMs;         // Time taken to process the detection

    // 🔹 Getters and setters
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

    public boolean isLikelyGenuine() {
        return likelyGenuine;
    }
    public void setLikelyGenuine(boolean likelyGenuine) {
        this.likelyGenuine = likelyGenuine;
    }

    public double getForgeryProbability() {
        return forgeryProbability;
    }
    public void setForgeryProbability(double forgeryProbability) {
        this.forgeryProbability = forgeryProbability;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    // 🔹 Optional fields for image detection (ELA heatmap)
    // Keep in subclass if only image detection uses them
    // private String elaHeatmapPath;
    // private String elaHeatmapBase64;
}