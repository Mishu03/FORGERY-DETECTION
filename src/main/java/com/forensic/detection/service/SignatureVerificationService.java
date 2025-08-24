package com.forensic.detection.service;

import com.forensic.detection.dto.SignatureVerificationResult;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SignatureVerificationService {

    public SignatureVerificationResult verifySignature(MultipartFile referenceFile, MultipartFile testFile) {
        try {
            // Save files temporarily
            Path refPath = Files.createTempFile("ref-sig-", ".tmp");
            Files.copy(referenceFile.getInputStream(), refPath, StandardCopyOption.REPLACE_EXISTING);
            
            Path testPath = Files.createTempFile("test-sig-", ".tmp");
            Files.copy(testFile.getInputStream(), testPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Load OpenCV
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            
            // Read images
            Mat refImage = Imgcodecs.imread(refPath.toString(), Imgcodecs.IMREAD_GRAYSCALE);
            Mat testImage = Imgcodecs.imread(testPath.toString(), Imgcodecs.IMREAD_GRAYSCALE);
            
            // Compare signatures
            double similarityScore = compareSignatures(refImage, testImage);
            
            // Prepare result
            SignatureVerificationResult result = new SignatureVerificationResult();
            result.setReferenceFileName(referenceFile.getOriginalFilename());
            result.setTestFileName(testFile.getOriginalFilename());
            result.setSimilarityScore(similarityScore);
            result.setLikelyGenuine(similarityScore >= 0.85);
            
            // Clean up
            Files.deleteIfExists(refPath);
            Files.deleteIfExists(testPath);
            
            return result;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to verify signatures: " + e.getMessage(), e);
        }
    }
    
    private double compareSignatures(Mat refImage, Mat testImage) {
        // Resize to same dimensions
        Size size = new Size(500, 200);
        Imgproc.resize(refImage, refImage, size);
        Imgproc.resize(testImage, testImage, size);
        
        // Binarize images
        Imgproc.threshold(refImage, refImage, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        Imgproc.threshold(testImage, testImage, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        
        // Calculate structural similarity
        Mat diff = new Mat();
        Core.absdiff(refImage, testImage, diff);
        Imgproc.threshold(diff, diff, 25, 255, Imgproc.THRESH_BINARY);
        
        int totalPixels = diff.rows() * diff.cols();
        int diffPixels = Core.countNonZero(diff);
        
        return 1.0 - ((double) diffPixels / totalPixels);
    }
}
