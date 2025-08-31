package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.ImageDetectionResult;
import com.mydomain.forgery_detection.service.analyzer.ElaAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.NoiseAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.MetadataAnalyzer;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImageDetectionService {

    private final ElaAnalyzer elaAnalyzer;
    private final NoiseAnalyzer noiseAnalyzer;
    private final MetadataAnalyzer metadataAnalyzer;

    public ImageDetectionService(ElaAnalyzer elaAnalyzer, NoiseAnalyzer noiseAnalyzer, MetadataAnalyzer metadataAnalyzer) {
        this.elaAnalyzer = elaAnalyzer;
        this.noiseAnalyzer = noiseAnalyzer;
        this.metadataAnalyzer = metadataAnalyzer;
    }

    public ImageDetectionResult analyze(MultipartFile file) {
        ImageDetectionResult result = new ImageDetectionResult();
        result.setFileName(file.getOriginalFilename());
        long start = System.currentTimeMillis();

        try {
            // Save MultipartFile to temp file (since OpenCV loads from file path)
            File tempFile = File.createTempFile("upload-", ".tmp");
            file.transferTo(tempFile);

            // Load using OpenCV
            Mat img = Imgcodecs.imread(tempFile.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);

            if (img.empty()) {
                result.setErrorMessage("Could not decode image");
            } else {
                double elaScore = elaAnalyzer.analyze(img);
                double noiseScore = noiseAnalyzer.analyze(img);
                double metadataScore = metadataAnalyzer.analyze(file);
                double similarityScore = 0.5; // Placeholder

                double forgeryProbability =
                        (0.4 * (1 - elaScore)) +
                        (0.3 * noiseScore) +
                        (0.2 * (1 - metadataScore)) +
                        (0.1 * (1 - similarityScore));

                String decision = forgeryProbability < 0.3 ? "Likely Genuine" :
                                  forgeryProbability < 0.7 ? "Suspicious" : "Likely Forged";

                result.setElaScore(elaScore);
                result.setNoiseAnalysisScore(noiseScore);
                result.setMetadataConsistencyScore(metadataScore);
                result.setSimilarityScore(similarityScore);
                result.setForgeryProbability(forgeryProbability);
                result.setDecision(decision);
                result.setLikelyGenuine(decision.equals("Likely Genuine"));
            }

            tempFile.delete();

        } catch (IOException e) {
            result.setErrorMessage("Unexpected error: " + e.getMessage());
            result.setForgeryProbability(0);
            result.setDecision("Unknown");
        }

        result.setProcessingTimeMs(System.currentTimeMillis() - start);
        return result;
    }
}





