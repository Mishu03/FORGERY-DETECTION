package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.SignatureVerificationResult;
import com.mydomain.forgery_detection.service.analyzer.PressurePatternAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.StrokeConsistencyAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.StructuralSimilarityAnalyzer;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SignatureVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(SignatureVerificationService.class);
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

    public SignatureVerificationResult verifySignature(Mat refImg, Mat testImg) {
        SignatureVerificationResult result = new SignatureVerificationResult();
        result.setFileName("referenceMat & testMat");
        result.setDetectionType("SIGNATURE");
        long start = System.currentTimeMillis();
        try {
            if (refImg == null || testImg == null || refImg.empty() || testImg.empty()) {
                result.setErrorMessage("One or both signature images are empty or null");
                result.setForgeryProbability(0);
                result.setDecision("Unknown");
                result.setLikelyGenuine(false);
                logger.warn("Empty or null Mat provided to verifySignature.");
                return result;
            }

            // Extract stroke lengths and pressure patterns
            double[] refStrokes = strokeAnalyzer.extractStrokeLengths(refImg);
            double[] testStrokes = strokeAnalyzer.extractStrokeLengths(testImg);

            // Stroke score: similarity between ref and test strokes
            double strokeScore = 1.0 - Math.abs(strokeAnalyzer.analyze(refStrokes) - strokeAnalyzer.analyze(testStrokes));

            double[] refPressure = pressureAnalyzer.extractPressureData(refImg);
            double[] testPressure = pressureAnalyzer.extractPressureData(testImg);

            // Pressure score: similarity between ref and test pressure
            double pressureScore = 1.0 - Math.abs(pressureAnalyzer.analyze(refPressure) - pressureAnalyzer.analyze(testPressure));

            // Structural similarity between reference and test signature
            double[][] refArray = structuralAnalyzer.matToDoubleArray(refImg);
            double[][] testArray = structuralAnalyzer.matToDoubleArray(testImg);
            double structuralScore = structuralAnalyzer.analyze(refArray, testArray);

            // Compute overall forgery probability
            double forgeryProbability = 1 - ((structuralScore + pressureScore + strokeScore) / 3.0);
            String decision = forgeryProbability < 0.3 ? "Likely Genuine" :
                              forgeryProbability < 0.7 ? "Suspicious" : "Likely Forged";

            logger.info("Signature Analysis -> Structural: {}, Stroke: {}, Pressure: {}, Decision: {}, Probability: {}",
                    structuralScore, strokeScore, pressureScore, decision, forgeryProbability);

            result.setStructuralSimilarity(structuralScore);
            result.setPressurePatternScore(pressureScore);
            result.setStrokeConsistencyScore(strokeScore);
            result.setForgeryProbability(forgeryProbability);
            result.setDecision(decision);
            result.setLikelyGenuine(decision.equals("Likely Genuine"));
        } catch (Exception e) {
            result.setErrorMessage("Unexpected error: " + e.getMessage());
            result.setForgeryProbability(0);
            result.setDecision("Unknown");
            result.setLikelyGenuine(false);
            logger.error("Error verifying signature", e);
        }
        result.setProcessingTimeMs(System.currentTimeMillis() - start);
        logger.debug("Signature processing time: {} ms", result.getProcessingTimeMs());
        return result;
    }
}
