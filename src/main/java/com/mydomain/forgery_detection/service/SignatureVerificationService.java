package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.SignatureVerificationResult;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class SignatureVerificationService {

    public SignatureVerificationResult verifySignature(MultipartFile referenceFile, MultipartFile testFile) {
        SignatureVerificationResult result = new SignatureVerificationResult();
        long startTime = System.currentTimeMillis();

        try {
            // Create temporary files for the uploaded signature images
            Path refPath = Files.createTempFile("ref-sig-", ".tmp");
            Files.copy(referenceFile.getInputStream(), refPath, StandardCopyOption.REPLACE_EXISTING);

            Path testPath = Files.createTempFile("test-sig-", ".tmp");
            Files.copy(testFile.getInputStream(), testPath, StandardCopyOption.REPLACE_EXISTING);

            // Load and preprocess images
            Mat refImage = preprocessSignature(Imgcodecs.imread(refPath.toString()));
            Mat testImage = preprocessSignature(Imgcodecs.imread(testPath.toString()));

            if (refImage.empty() || testImage.empty()) {
                throw new RuntimeException("Failed to load signature images");
            }

            // Perform comparisons
            double structuralSimilarity = calculateStructuralSimilarity(refImage, testImage);
            double pressureScore = analyzePressurePatterns(refImage, testImage);
            double strokeScore = analyzeStrokeConsistency(refImage, testImage);

            // Set results
            result.setStructuralSimilarity(structuralSimilarity);
            result.setPressurePatternScore(pressureScore);
            result.setStrokeConsistencyScore(strokeScore);
            result.setFileName(referenceFile.getOriginalFilename() + " vs " + testFile.getOriginalFilename());
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            // Cleanup temporary files
            Files.deleteIfExists(refPath);
            Files.deleteIfExists(testPath);

        } catch (IOException e) {
            result.setErrorMessage("File processing error: " + e.getMessage());
        } catch (Exception e) {
            result.setErrorMessage("Verification error: " + e.getMessage());
        }

        return result;
    }

    private Mat preprocessSignature(Mat image) {
        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        
        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);
        
        // Thresholding to create a binary image
        Imgproc.threshold(gray, gray, 128, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        
        return gray; // Return processed image
    }

    private double calculateStructuralSimilarity(Mat refImage, Mat testImage) {
        // Implement structural similarity calculation logic
        // Placeholder for actual implementation
        return Imgproc.compareHist(refImage, testImage, Imgproc.CV_COMP_CORREL);
    }

    private double analyzePressurePatterns(Mat refImage, Mat testImage) {
        // Implement pressure pattern analysis logic
        // Placeholder for actual implementation
        return 0.8; // Example score
    }

    private double analyzeStrokeConsistency(Mat refImage, Mat testImage) {
        // Implement stroke consistency analysis logic
        // Placeholder for actual implementation
        return 0.85; // Example score
    }
}
