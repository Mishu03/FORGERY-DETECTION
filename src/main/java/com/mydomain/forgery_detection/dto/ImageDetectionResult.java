package com.mydomain.forgery_detection.dto;

public class ImageDetectionResult extends DetectionResult {
    private double elaScore;
    private double noiseAnalysisScore;
    private double metadataConsistencyScore;
    private double similarityScore;

    // Optional: Path to ELA heatmap on server
    private String elaHeatmapPath;

    // Optional: Base64 encoded ELA heatmap (for sending in JSON)
    private String elaHeatmapBase64;

    // Getters and Setters
    public double getElaScore() {
        return elaScore;
    }

    public void setElaScore(double elaScore) {
        this.elaScore = elaScore;
    }

    public double getNoiseAnalysisScore() {
        return noiseAnalysisScore;
    }

    public void setNoiseAnalysisScore(double noiseAnalysisScore) {
        this.noiseAnalysisScore = noiseAnalysisScore;
    }

    public double getMetadataConsistencyScore() {
        return metadataConsistencyScore;
    }

    public void setMetadataConsistencyScore(double metadataConsistencyScore) {
        this.metadataConsistencyScore = metadataConsistencyScore;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getElaHeatmapPath() {
        return elaHeatmapPath;
    }

    public void setElaHeatmapPath(String elaHeatmapPath) {
        this.elaHeatmapPath = elaHeatmapPath;
    }

    public String getElaHeatmapBase64() {
        return elaHeatmapBase64;
    }

    public void setElaHeatmapBase64(String elaHeatmapBase64) {
        this.elaHeatmapBase64 = elaHeatmapBase64;
    }

    // Optional: convenience method to set all main scores at once
    public void setAllScores(double elaScore, double noiseScore, double metadataScore, double similarityScore) {
        this.elaScore = elaScore;
        this.noiseAnalysisScore = noiseScore;
        this.metadataConsistencyScore = metadataScore;
        this.similarityScore = similarityScore;
    }
}
