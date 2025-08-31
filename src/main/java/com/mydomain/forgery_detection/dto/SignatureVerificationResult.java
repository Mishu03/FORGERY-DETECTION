package com.mydomain.forgery_detection.dto;

public class SignatureVerificationResult extends DetectionResult {
    private double structuralSimilarity;
    private double pressurePatternScore;
    private double strokeConsistencyScore;

    // Getters and Setters
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
}
