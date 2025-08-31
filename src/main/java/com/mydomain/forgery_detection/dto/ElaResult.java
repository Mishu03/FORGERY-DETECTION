package com.mydomain.forgery_detection.dto;

public class ElaResult {
    private double score;
    private String heatmapPath;

    public ElaResult(double score, String heatmapPath) {
        this.score = score;
        this.heatmapPath = heatmapPath;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getHeatmapPath() {
        return heatmapPath;
    }

    public void setHeatmapPath(String heatmapPath) {
        this.heatmapPath = heatmapPath;
    }
}