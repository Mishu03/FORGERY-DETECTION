package com.mydomain.forgery_detection.exception;

import com.mydomain.forgery_detection.dto.ApiResponse;
import com.mydomain.forgery_detection.dto.DetectionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<DetectionResult>> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // optional: log the full stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unexpected error: " + ex.getMessage()));
    }

    // You can add more specific handlers if needed
    // @ExceptionHandler(IOException.class)
    // public ResponseEntity<ApiResponse<DetectionResult>> handleIOException(IOException ex) { ... }
}
