package com.mydomain.forgery_detection.controller;

import com.mydomain.forgery_detection.dto.ImageDetectionResult;
import com.mydomain.forgery_detection.dto.VideoDetectionResult;
import com.mydomain.forgery_detection.dto.SignatureVerificationResult;
import com.mydomain.forgery_detection.service.ImageDetectionService;
import com.mydomain.forgery_detection.service.VideoDetectionService;
import com.mydomain.forgery_detection.service.SignatureVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/detection")
public class DetectionController {

    private final ImageDetectionService imageDetectionService;
    private final VideoDetectionService videoDetectionService;
    private final SignatureVerificationService signatureVerificationService;

    public DetectionController(
            ImageDetectionService imageDetectionService,
            VideoDetectionService videoDetectionService,
            SignatureVerificationService signatureVerificationService) {
        this.imageDetectionService = imageDetectionService;
        this.videoDetectionService = videoDetectionService;
        this.signatureVerificationService = signatureVerificationService;
    }

    // 🔹 Image detection endpoint
    @PostMapping("/image")
    public ResponseEntity<ImageDetectionResult> analyzeImage(
            @RequestParam("file") MultipartFile file) {
        ImageDetectionResult result = imageDetectionService.analyzeImage(file);
        return ResponseEntity.ok(result);
    }

    // 🔹 Video detection endpoint
    @PostMapping("/video")
    public ResponseEntity<VideoDetectionResult> analyzeVideo(
            @RequestParam("file") MultipartFile videoFile) { // Use "file" to match frontend
        VideoDetectionResult result = videoDetectionService.analyzeVideo(videoFile);
        return ResponseEntity.ok(result);
    }

    // 🔹 Signature verification endpoint
    @PostMapping("/signature")
    public ResponseEntity<SignatureVerificationResult> verifySignature(
            @RequestParam("referenceFile") MultipartFile referenceFile,
            @RequestParam("testFile") MultipartFile testFile) {
        SignatureVerificationResult result = signatureVerificationService.verifySignature(referenceFile, testFile);
        return ResponseEntity.ok(result);
    }
}