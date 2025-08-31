package com.mydomain.forgery_detection.dto;

public class ImageDetectionResult extends DetectionResult {

    private double elaScore;
    private double noiseAnalysisScore;
    private double metadataConsistencyScore;

    public double getElaScore() { return elaScore; }
    public void setElaScore(double elaScore) { this.elaScore = elaScore; }

    public double getNoiseAnalysisScore() { return noiseAnalysisScore; }
    public void setNoiseAnalysisScore(double noiseAnalysisScore) { this.noiseAnalysisScore = noiseAnalysisScore; }

    public double getMetadataConsistencyScore() { return metadataConsistencyScore; }
    public void setMetadataConsistencyScore(double metadataConsistencyScore) { this.metadataConsistencyScore = metadataConsistencyScore; }
}

