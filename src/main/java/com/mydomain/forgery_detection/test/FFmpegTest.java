package com.mydomain.forgery_detection.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FFmpegTest {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("FFmpeg exit code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FFmpeg is not recognized. Check PATH.");
        }
    }
}