package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AudioVideoSyncAnalyzer {

    public double analyze(File videoFile) {
        if (videoFile == null || !videoFile.exists()) return 0.0;

        try {
            // Step 1: Extract audio waveform to raw PCM using FFmpeg
            File tempAudio = File.createTempFile("audio-", ".pcm");
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-y", "-i", videoFile.getAbsolutePath(),
                    "-ac", "1", "-ar", "44100", "-f", "s16le", tempAudio.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            // Step 2: Convert PCM to energy events
            List<Double> audioEvents = extractAudioEnergy(tempAudio);
            tempAudio.delete();

            // Step 3: Extract video motion events
            List<Double> videoEvents = extractVideoMotion(videoFile);

            // Step 4: Compare events
            return compareEvents(audioEvents, videoEvents);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private List<Double> extractAudioEnergy(File pcmFile) throws IOException {
        List<Double> events = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(pcmFile)))) {
            int windowSize = 4410; // 0.1s window at 44100 Hz
            byte[] buffer = new byte[windowSize * 2]; // 2 bytes per sample
            double time = 0.0;

            int bytesRead;
            while ((bytesRead = dis.read(buffer)) != -1) {
                int samplesRead = bytesRead / 2;
                double sum = 0.0;

                for (int i = 0; i < samplesRead; i++) {
                    int low = buffer[2 * i] & 0xFF;
                    int high = buffer[2 * i + 1];
                    short sample = (short) ((high << 8) | low); // little-endian
                    sum += sample * sample;
                }

                double rms = Math.sqrt(sum / samplesRead);
                if (rms > 1000) { // threshold for detecting audio activity
                    events.add(time);
                }
                time += 0.1;
            }
        }
        return events;
    }

    private List<Double> extractVideoMotion(File videoFile) {
        List<Double> events = new ArrayList<>();
        VideoCapture capture = new VideoCapture(videoFile.getAbsolutePath());
        if (!capture.isOpened()) return events;

        try {
            Mat prev = new Mat();
            Mat curr = new Mat();
            Mat diff = new Mat();

            double fps = capture.get(org.opencv.videoio.Videoio.CAP_PROP_FPS);
            if (fps <= 0) fps = 25;
            double frameTime = 1.0 / fps;

            if (!capture.read(prev)) return events;
            double time = 0.0;

            while (capture.read(curr)) {
                Core.absdiff(prev, curr, diff);
                double meanDiff = Core.mean(diff).val[0] / 255.0;
                if (meanDiff > 0.03) { // detect motion
                    events.add(time);
                }
                prev = curr.clone();
                time += frameTime;
            }

        } finally {
            capture.release();
        }
        return events;
    }

    private double compareEvents(List<Double> audioEvents, List<Double> videoEvents) {
        if (audioEvents.isEmpty() || videoEvents.isEmpty()) return 0.0;

        int matched = 0;
        double tolerance = 0.3; // ±0.3s tolerance
        for (double aTime : audioEvents) {
            for (double vTime : videoEvents) {
                if (Math.abs(aTime - vTime) <= tolerance) {
                    matched++;
                    break;
                }
            }
        }
        return Math.min(1.0, (double) matched / audioEvents.size());
    }
}