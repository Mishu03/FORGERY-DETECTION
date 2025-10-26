package com.mydomain.forgery_detection.ml;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class MLClient {

    private static final String ML_URL = "http://localhost:5000/analyze";
    private static final int CONNECT_TIMEOUT = 5000; // 5 seconds
    private static final int READ_TIMEOUT = 10000;   // 10 seconds

    public static JSONObject analyzeImage(File imageFile) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        URL url = new URL(ML_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream output = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {

            // Detect MIME type
            String contentType = Files.probeContentType(imageFile.toPath());
            if (contentType == null) contentType = "application/octet-stream";

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(imageFile.getName()).append("\"\r\n");
            writer.append("Content-Type: ").append(contentType).append("\r\n\r\n").flush();

            Files.copy(imageFile.toPath(), output);
            output.flush();

            writer.append("\r\n--").append(boundary).append("--\r\n").flush();
        }

        // Read response
        int status = conn.getResponseCode();
        InputStream responseStream = (status == 200) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) response.append(line);
        }

        if (status != 200) {
            throw new IOException("ML service returned status " + status + ": " + response.toString());
        }

        return new JSONObject(response.toString());
    }

    // Example usage
    public static void main(String[] args) {
        try {
            File testImage = new File("A:/MJR/forgery-detection/test_images/sample.jpg");
            JSONObject result = analyzeImage(testImage);
            System.out.println("ML Result: " + result.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
