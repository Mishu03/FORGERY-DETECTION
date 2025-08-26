package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.ImageDetectionResult;
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
public class ImageDetectionService {

    public ImageDetectionResult analyzeImage(MultipartFile imageFile) {
        ImageDetectionResult result = new ImageDetectionResult();
        long startTime = System.currentTimeMillis();

        try {
            // Save temporary file
            Path tempFile = Files.createTempFile("image-", ".tmp");
            Files.copy(imageFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Load image
            Mat image = Imgcodecs.imread(tempFile.toString());
            if (image.empty()) {
                throw new RuntimeException("Failed to load image");
            }

            // Perform various analyses
            double elaScore = performELAAnalysis(image);
            double noiseScore = performNoiseAnalysis(image);
            double metadataScore = 0.8; // Placeholder for metadata analysis

            // Calculate overall similarity score
            double similarityScore = (elaScore * 0.4) + (noiseScore * 0.4) + (metadataScore * 0.2);

            result.setFileName(imageFile.getOriginalFilename());
            result.setElaScore(elaScore);
            result.setNoiseAnalysisScore(noiseScore);
            result.setMetadataConsistencyScore(metadataScore);
            result.setSimilarityScore(similarityScore);
            result.setLikelyGenuine(similarityScore >= 0.7);
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            // Cleanup
            Files.deleteIfExists(tempFile);

        } catch (IOException e) {
            result.setErrorMessage("File processing error: " + e.getMessage());
        } catch (Exception e) {
            result.setErrorMessage("Analysis error: " + e.getMessage());
        }

        return result;
    }

    private double performELAAnalysis(Mat image) {
        try {
            // Convert to grayscale
            Mat gray = new Mat();
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
            
            // Save original as JPEG with quality 90
            MatOfInt params = new MatOfInt(
                Imgcodecs.IMWRITE_JPEG_QUALITY, 90
            );
            Imgcodecs.imwrite("temp_original.jpg", gray, params);
            
            // Reload the compressed image
            Mat compressed = Imgcodecs.imread("temp_original.jpg", Imgcodecs.IMREAD_GRAYSCALE);
            
            // Calculate difference (ELA)
            Mat diff = new Mat();
            Core.absdiff(gray, compressed, diff);
            
            // Calculate mean difference as score (normalized)
            Scalar meanDiff = Core.mean(diff);
            double elaScore = 1.0 - (meanDiff.val[0] / 255.0);
            
            // Cleanup
            gray.release();
            compressed.release();
            diff.release();
            
            return Math.max(0, Math.min(1.0, elaScore));
            
        } catch (Exception e) {
            return 0.5; // Default score if analysis fails
        }
    }

    private double performNoiseAnalysis(Mat image) {
        try {
            // Convert to grayscale
            Mat gray = new Mat();
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
            
            // Apply Gaussian blur
            Mat blurred = new Mat();
            Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);
            
            // Calculate difference
            Mat diff = new Mat();
            Core.absdiff(gray, blurred, diff);
            
            // Calculate noise level
            Scalar meanNoise = Core.mean(diff);
            double noiseLevel = meanNoise.val[0] / 255.0;
            
            // Higher noise might indicate tampering
            double noiseScore = 1.0 - noiseLevel;
            
            // Cleanup
            gray.release();
            blurred.release();
            diff.release();
            
            return Math.max(0, Math.min(1.0, noiseScore));
            
        } catch (Exception e) {
            return 0.5;
        }
    }
}
