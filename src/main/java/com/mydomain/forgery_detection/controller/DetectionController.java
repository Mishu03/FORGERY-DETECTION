package com.mydomain.forgery_detection.controller;

import com.mydomain.forgery_detection.dto.ImageDetectionResult;
import com.mydomain.forgery_detection.dto.VideoDetectionResult;
import com.mydomain.forgery_detection.dto.SignatureVerificationResult;
import com.mydomain.forgery_detection.service.ImageDetectionService;
import com.mydomain.forgery_detection.service.VideoDetectionService;
import com.mydomain.forgery_detection.service.SignatureVerificationService;
import com.mydomain.forgery_detection.ml.MLClient;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/detection")
public class DetectionController {

    private final ImageDetectionService imageDetectionService;
    private final VideoDetectionService videoDetectionService;
    private final SignatureVerificationService signatureVerificationService;

    public DetectionController(ImageDetectionService imageDetectionService,
                               VideoDetectionService videoDetectionService,
                               SignatureVerificationService signatureVerificationService) {
        this.imageDetectionService = imageDetectionService;
        this.videoDetectionService = videoDetectionService;
        this.signatureVerificationService = signatureVerificationService;
    }

    // --- Image detection (with ML integration) ---
    @PostMapping("/image")
    public ResponseEntity<ImageDetectionResult> analyzeImage(@RequestParam("file") MultipartFile file) {
        ImageDetectionResult result = imageDetectionService.analyzeImage(file);

        // --- Call MLClient for ML inference ---
        try {
            File tempFile = File.createTempFile("uploaded-", ".png");
            file.transferTo(tempFile);

            JSONObject mlResult = MLClient.analyzeImage(tempFile);
            tempFile.deleteOnExit();

            // Merge ML results into ImageDetectionResult
            result.setMlForgeryScore(mlResult.optDouble("ml_forgery_score", -1));
            result.setMlConfidence(mlResult.optDouble("confidence", -1));
            result.setMlMessage(mlResult.optString("message", "ML service not available"));

        } catch (Exception e) {
            // If ML service fails, populate fallback info
            result.setMlMessage("Failed to call ML service: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    // --- Video detection ---
    @PostMapping("/video")
    public ResponseEntity<VideoDetectionResult> analyzeVideo(@RequestParam("file") MultipartFile videoFile) {
        VideoDetectionResult result = videoDetectionService.analyzeVideo(videoFile);
        return ResponseEntity.ok(result);
    }

    // --- Signature verification ---
    @PostMapping("/signature")
    public ResponseEntity<SignatureVerificationResult> verifySignature(
            @RequestParam("referenceFile") MultipartFile referenceFile,
            @RequestParam("testFile") MultipartFile testFile) {

        try {
            // Convert MultipartFile to temp Files
            File refTemp = File.createTempFile("refSig-", ".png");
            referenceFile.transferTo(refTemp);
            File testTemp = File.createTempFile("testSig-", ".png");
            testFile.transferTo(testTemp);

            // Read Mats from file paths
            Mat refMat = Imgcodecs.imread(refTemp.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
            Mat testMat = Imgcodecs.imread(testTemp.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

            refTemp.deleteOnExit();
            testTemp.deleteOnExit();

            // Call updated service method that accepts Mats
            SignatureVerificationResult result = signatureVerificationService.verifySignature(refMat, testMat);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            SignatureVerificationResult errorResult = new SignatureVerificationResult();
            errorResult.setErrorMessage("Failed to process signature files: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
}
