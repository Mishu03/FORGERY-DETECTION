package com.mydomain.forgery_detection.service.analyzer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Core;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ElaAnalyzer {

    /**
     * Returns an ELA score between 0 and 1
     */
    public double analyze(Mat image) {
        try {
            // Save image to temporary JPEG with 90% quality
            String tempFile = System.getProperty("java.io.tmpdir") + File.separator + "ela_temp.jpg";
            Imgcodecs.imwrite(tempFile, image, new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 90));

            // Reload the compressed image
            Mat compressed = Imgcodecs.imread(tempFile);

            // Compute absolute difference
            Mat diff = new Mat();
            Core.absdiff(image, compressed, diff);

            // Convert to grayscale and normalize
            Mat gray = new Mat();
            Imgproc.cvtColor(diff, gray, Imgproc.COLOR_BGR2GRAY);
            Scalar sumScalar = Core.sumElems(gray);
            double totalDiff = sumScalar.val[0] / (gray.rows() * gray.cols() * 255);

            return Math.min(1.0, totalDiff * 10); // Scale to 0-1 range
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}




