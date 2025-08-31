package com.mydomain.forgery_detection.service.analyzer;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AudioVideoSyncAnalyzer {

    /**
     * Returns audio-video sync score (0-1)
     */
    public double analyze(File videoFile) {
        try {
            // Placeholder: always return 1
            return 1.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}

