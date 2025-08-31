package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.DetectionResult;
import com.mydomain.forgery_detection.dto.SignatureVerificationResult;
import com.mydomain.forgery_detection.service.analyzer.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class SignatureVerificationService {

    private final StructuralSimilarityAnalyzer structuralAnalyzer;
    private final PressurePatternAnalyzer pressureAnalyzer;
    private final StrokeConsistencyAnalyzer strokeAnalyzer;

    public SignatureVerificationService(StructuralSimilarityAnalyzer structuralAnalyzer,
                                        PressurePatternAnalyzer pressureAnalyzer,
                                        StrokeConsistencyAnalyzer strokeAnalyzer) {
        this.structuralAnalyzer = structuralAnalyzer;
        this.pressureAnalyzer = pressureAnalyzer;
        this.strokeAnalyzer = strokeAnalyzer;
    }

    public DetectionResult verifySignature(MultipartFile referenceFile, MultipartFile testFile) {
        SignatureVerificationResult result = new SignatureVerificationResult();
        result.setFileName(
                (referenceFile != null ? referenceFile.getOriginalFilename() : "ref") + "&" +
                (testFile != null ? testFile.getOriginalFilename() : "test")
        );
        result.setDetectionType("SIGNATURE");
        long start = System.currentTimeMillis();

        try {
            File refTemp = File.createTempFile("sig-ref-", ".png");
            File testTemp = File.createTempFile("sig-test-", ".png");

            if (referenceFile != null) referenceFile.transferTo(refTemp);
            if (testFile != null) testFile.transferTo(testTemp);

            Mat refImg = Imgcodecs.imread(refTemp.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
            Mat testImg = Imgcodecs.imread(testTemp.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

            if (refImg.empty() || testImg.empty()) {
                result.setErrorMessage("Failed to decode one or both signature images");
                result.setForgeryProbability(0);
                result.setDecision("Unknown");
                result.setLikelyGenuine(false);
                return result;
            }

            double structural = structuralAnalyzer.analyze(refImg, testImg);
            double pressure = pressureAnalyzer.analyze(refImg, testImg);
            double stroke = strokeAnalyzer.analyze(refImg, testImg);

            double forgeryProbability = 1 - ((structural + pressure + stroke) / 3.0);
            String decision = forgeryProbability < 0.3 ? "Likely Genuine" :
                              forgeryProbability < 0.7 ? "Suspicious" : "Likely Forged";

            result.setStructuralSimilarity(structural);
            result.setPressurePatternScore(pressure);
            result.setStrokeConsistencyScore(stroke);
            result.setForgeryProbability(forgeryProbability);
            result.setDecision(decision);
            result.setLikelyGenuine(decision.equals("Likely Genuine"));

            refTemp.delete();
            testTemp.delete();

        } catch (IOException e) {
            result.setErrorMessage("Unexpected error: " + e.getMessage());
            result.setForgeryProbability(0);
            result.setDecision("Unknown");
            result.setLikelyGenuine(false);
        }

        result.setProcessingTimeMs(System.currentTimeMillis() - start);
        return result;
    }
}




