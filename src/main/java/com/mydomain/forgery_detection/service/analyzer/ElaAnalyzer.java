package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

@Component
public class ElaAnalyzer {
    // Returns a score between 0 (likely forged) and 1 (likely genuine)
    public double analyze(Mat original) throws Exception {
        // Step 1: Save as JPEG at 90% quality
        File tempFile = File.createTempFile("ela-", ".jpg");
        Imgcodecs.imwrite(tempFile.getAbsolutePath(), original,
                new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 90));
        // Step 2: Reload compressed image
        Mat compressed = Imgcodecs.imread(tempFile.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
        // Step 3: Absolute difference
        Mat diff = new Mat();
        Core.absdiff(original, compressed, diff);
        // Step 4: Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(diff, gray, Imgproc.COLOR_BGR2GRAY);
        // Step 5: Normalize
        Core.MinMaxLocResult minMax = Core.minMaxLoc(gray);
        double maxVal = minMax.maxVal;
        if (maxVal > 0) {
            gray.convertTo(gray, CvType.CV_32F, 1.0 / maxVal);
        }
        Scalar mean = Core.mean(gray);
        tempFile.delete();
        // Higher mean → higher difference → more suspicious
        return 1.0 - mean.val[0];  // closer to 1 → likely genuine
    }
    
    // Generate ELA heatmap for visualization
    public BufferedImage generateElaHeatmap(Mat original) throws Exception {
        // Step 1: Save as JPEG at 90% quality
        File tempFile = File.createTempFile("ela-", ".jpg");
        Imgcodecs.imwrite(tempFile.getAbsolutePath(), original,
                new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 90));
        // Step 2: Reload compressed image
        Mat compressed = Imgcodecs.imread(tempFile.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
        // Step 3: Absolute difference
        Mat diff = new Mat();
        Core.absdiff(original, compressed, diff);
        // Step 4: Amplify differences for visualization
        Mat amplified = new Mat();
        diff.convertTo(amplified, CvType.CV_8UC3, 10.0); // amplify factor = 10
        BufferedImage heatmap = matToBufferedImage(amplified);
        tempFile.delete();
        return heatmap;
    }
    
    // Helper: convert OpenCV Mat to BufferedImage
    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_3BYTE_BGR;
        if (mat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        }
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] data = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
        return image;
    }
}
