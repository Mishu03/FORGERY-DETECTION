package com.mydomain.forgery_detection.service;

import com.mydomain.forgery_detection.dto.ImageDetectionResult;
import com.mydomain.forgery_detection.service.analyzer.ElaAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.MetadataAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.NoiseAnalyzer;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Map;



@Service
public class ImageDetectionService {
    private static final Logger logger = LoggerFactory.getLogger(ImageDetectionService.class);
    private final ElaAnalyzer elaAnalyzer;
    private final NoiseAnalyzer noiseAnalyzer;
    private final MetadataAnalyzer metadataAnalyzer;

    @Value("${forgery.threshold.genuine:0.3}")
    private double genuineThreshold;

    @Value("${forgery.threshold.suspicious:0.7}")
    private double suspiciousThreshold;

    @Value("${ml.service.url:http://127.0.0.1:5000/analyze}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ImageDetectionService(ElaAnalyzer elaAnalyzer,
                                 NoiseAnalyzer noiseAnalyzer,
                                 MetadataAnalyzer metadataAnalyzer) {
        this.elaAnalyzer = elaAnalyzer;
        this.noiseAnalyzer = noiseAnalyzer;
        this.metadataAnalyzer = metadataAnalyzer;
    }

    public ImageDetectionResult analyzeImage(MultipartFile file) {
        ImageDetectionResult result = new ImageDetectionResult();
        result.setFileName(file.getOriginalFilename());
        result.setDetectionType("IMAGE");
        long start = System.currentTimeMillis();
        double mlForgeryScore = 0.5; // default fallback

        try {
            // Create temporary file
            File tempFile = File.createTempFile("upload-", ".tmp");
            file.transferTo(tempFile);
            logger.debug("Temporary file created at: {}", tempFile.getAbsolutePath());

            // Read image
            Mat img = Imgcodecs.imread(tempFile.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
            if (img.empty()) {
                result.setErrorMessage("Could not decode image");
                result.setForgeryProbability(0);
                result.setDecision("Unknown");
                result.setLikelyGenuine(false);
                logger.warn("Image could not be read: {}", file.getOriginalFilename());
            } else {
                // ----- ANALYSIS -----
                double elaScore = elaAnalyzer.analyze(img);            // 0-1, higher → more genuine
                double noiseScore = noiseAnalyzer.analyze(img);        // 0-1, higher → suspicious
                double metadataScore = metadataAnalyzer.analyze(file); // 0-1, higher → consistent
                double similarityScore = computeStructuralSimilarity(img); // 0-1, higher → similar

                logger.debug("Analyzer Scores -> ELA: {}, Noise: {}, Metadata: {}, Similarity: {}",
                        elaScore, noiseScore, metadataScore, similarityScore);

                // ----- CALL PYTHON ML SERVICE -----
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("file", file.getResource());

                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                    ResponseEntity<Map> response = restTemplate.exchange(pythonApiUrl, HttpMethod.POST, requestEntity, Map.class);

                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        mlForgeryScore = ((Number) response.getBody().get("ml_forgery_score")).doubleValue();
                        logger.debug("ML Score from Python: {}", mlForgeryScore);
                    } else {
                        logger.warn("Python API returned status: {}", response.getStatusCode());
                    }
                } catch (Exception e) {
                    logger.error("Failed to call Python ML service: {}", e.getMessage());
                    // fallback score = 0.5
                }

                // ----- FORGERY PROBABILITY -----
                double forgeryProbability =
                        0.3 * (1 - elaScore) +
                        0.2 * noiseScore +
                        0.2 * (1 - metadataScore) +
                        0.1 * (1 - similarityScore) +
                        0.2 * mlForgeryScore; // include ML score

                forgeryProbability = Math.min(1.0, Math.pow(forgeryProbability, 1.2));

                // ----- DECISION -----
                String decision = forgeryProbability < genuineThreshold ? "Likely Genuine" :
                                  forgeryProbability < suspiciousThreshold ? "Suspicious" : "Likely Forged";
                logger.info("Decision: {}, Forgery Probability: {}", decision, forgeryProbability);

                // ----- ELA HEATMAP -----
                BufferedImage heatmap = elaAnalyzer.generateElaHeatmap(img);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(heatmap, "png", baos);
                String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

                // ----- POPULATE RESULT -----
                result.setElaScore(elaScore);
                result.setNoiseAnalysisScore(noiseScore);
                result.setMetadataConsistencyScore(metadataScore);
                result.setSimilarityScore(similarityScore);
                result.setMlForgeryScore(mlForgeryScore);
                result.setForgeryProbability(forgeryProbability);
                result.setDecision(decision);
                result.setLikelyGenuine(decision.equals("Likely Genuine"));
                result.setElaHeatmapBase64(base64);
            }
            tempFile.delete();
        } catch (Exception e) {
            result.setErrorMessage("Unexpected error: " + e.getMessage());
            result.setForgeryProbability(0);
            result.setDecision("Unknown");
            result.setLikelyGenuine(false);
            logger.error("Error analyzing image: {}", file.getOriginalFilename(), e);
        }
        result.setProcessingTimeMs(System.currentTimeMillis() - start);
        logger.debug("Processing time: {} ms", result.getProcessingTimeMs());
        return result;
    }

    private double computeStructuralSimilarity(Mat img) {
        // TODO: implement real structural similarity metric
        return 0.5; // placeholder
    }
}