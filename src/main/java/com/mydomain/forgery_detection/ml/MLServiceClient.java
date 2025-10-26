package com.mydomain.forgery_detection.ml;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.json.JSONObject;

public class MLServiceClient {

    private static final String ML_URL = "http://localhost:5000/analyze";

    public static JSONObject analyzeImage(Path imagePath) throws IOException, InterruptedException {
        // Generate random boundary for multipart
        String boundary = "----JavaBoundary" + UUID.randomUUID().toString();

        // Read file bytes
        byte[] imageBytes = Files.readAllBytes(imagePath);

        // Build multipart body
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
          .append(imagePath.getFileName())
          .append("\"\r\n");
        sb.append("Content-Type: image/jpeg\r\n\r\n");

        byte[] headerBytes = sb.toString().getBytes();
        byte[] footerBytes = ("\r\n--" + boundary + "--\r\n").getBytes();

        // Concatenate all parts
        byte[] bodyBytes = new byte[headerBytes.length + imageBytes.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, bodyBytes, 0, headerBytes.length);
        System.arraycopy(imageBytes, 0, bodyBytes, headerBytes.length, imageBytes.length);
        System.arraycopy(footerBytes, 0, bodyBytes, headerBytes.length + imageBytes.length, footerBytes.length);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(ML_URL))
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse JSON response
        return new JSONObject(response.body());
    }

    // Example usage
    public static void main(String[] args) {
        try {
            Path imagePath = Path.of("A:/MJR/forgery-detection/test_images/sample.jpg");
            JSONObject result = analyzeImage(imagePath);
            System.out.println("ML Response: " + result.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

