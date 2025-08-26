package com.mydomain.forgery_detection.controller;

import com.mydomain.forgery_detection.dto.*;
import com.mydomain.forgery_detection.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/detect")
@CrossOrigin(origins = "*")
public class DetectionController {

    @Autowired
    private ImageDetectionService imageDetectionService;

    @Autowired
    private VideoDetectionService videoDetectionService;

    @Autowired
    private SignatureVerificationService signatureVerificationService;

    @Autowired
    private DataSource dataSource; // Add DataSource for database connection

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<ImageDetectionResult>> detectImageForgery(
            @RequestParam("file") MultipartFile file) {
        try {
            ImageDetectionResult result = imageDetectionService.analyzeImage(file);
            return ResponseEntity.ok(ApiResponse.success("Image analyzed successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error analyzing image: " + e.getMessage()));
        }
    }

    @PostMapping("/video")
    public ResponseEntity<ApiResponse<VideoDetectionResult>> detectVideoForgery(
            @RequestParam("file") MultipartFile file) {
        try {
            VideoDetectionResult result = videoDetectionService.analyzeVideo(file);
            return ResponseEntity.ok(ApiResponse.success("Video analyzed successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error analyzing video: " + e.getMessage()));
        }
    }

    @PostMapping("/signature")
    public ResponseEntity<ApiResponse<SignatureVerificationResult>> verifySignature(
            @RequestParam("reference") MultipartFile referenceFile,
            @RequestParam("test") MultipartFile testFile) {
        try {
            SignatureVerificationResult result = signatureVerificationService.verifySignature(referenceFile, testFile);
            return ResponseEntity.ok(ApiResponse.success("Signature verification completed", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error verifying signature: " + e.getMessage()));
        }
    }

    // New endpoint to check database connection
    @GetMapping("/check-connection")
    public ResponseEntity<String> checkConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return ResponseEntity.ok("Database connection is successful!");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Database connection failed: " + e.getMessage());
        }
    }
}
