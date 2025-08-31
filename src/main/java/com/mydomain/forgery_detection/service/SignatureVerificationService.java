package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.SignatureVerificationResult;
import com.mydomain.forgery_detection.service.analyzer.PressurePatternAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.StrokeConsistencyAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.StructuralSimilarityAnalyzer;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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

    public SignatureVerificationResult verifySignature(MultipartFile referenceFile, MultipartFile testFile) {
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

            logger.debug("Temporary signature files created: {}, {}", refTemp.getAbsolutePath(), testTemp.getAbsolutePath());

            Mat refImg = Imgcodecs.imread(refTemp.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
            Mat testImg = Imgcodecs.imread(testTemp.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

            if (refImg.empty() || testImg.empty()) {
                result.setErrorMessage("Failed to decode one or both signature images");
                result.setForgeryProbability(0);
                result.setDecision("Unknown");
                result.setLikelyGenuine(false);
                logger.warn("One or both signature images could not be read.");
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

            // Cleanup temp files
            refTemp.delete();
            testTemp.delete();

        } catch (IOException e) {
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