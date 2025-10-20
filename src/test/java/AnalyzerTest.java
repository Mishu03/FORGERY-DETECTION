
import com.mydomain.forgery_detection.service.analyzer.ElaAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.NoiseAnalyzer;
import com.mydomain.forgery_detection.service.analyzer.MetadataAnalyzer;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;

public class AnalyzerTest {

    public static void main(String[] args) throws Exception {
        // Load OpenCV native library
        System.load("C:\\Users\\msnai\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
        System.out.println("OpenCV loaded successfully!");

        // Path to your test image
        String imagePath = "A:\\MJR\\forgery-detection\\test-data\\image.jpg";

        // Load image with OpenCV
        Mat img = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR);
        if (img.empty()) {
            System.out.println("Failed to load image!");
            return;
        }
        System.out.println("Image read successfully! Cols: " + img.cols() + ", Rows: " + img.rows() + ", Channels: " + img.channels());

        // Instantiate analyzers
        ElaAnalyzer elaAnalyzer = new ElaAnalyzer();
        NoiseAnalyzer noiseAnalyzer = new NoiseAnalyzer();
        MetadataAnalyzer metadataAnalyzer = new MetadataAnalyzer();

        // Create a MultipartFile mock for metadata analyzer
        File file = new File(imagePath);
        FileInputStream fis = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                fis
        );

        // Run analyzers
        double elaScore = elaAnalyzer.analyze(img);
        double noiseScore = noiseAnalyzer.analyze(img);
        double metadataScore = metadataAnalyzer.analyze(multipartFile);

        // Print results
        System.out.println("=== Analyzer Scores ===");
        System.out.println("ELA Score: " + elaScore);
        System.out.println("Noise Score: " + noiseScore);
        System.out.println("Metadata Score: " + metadataScore);

        // Optional: generate ELA heatmap
        System.out.println("Generating ELA heatmap...");
        var heatmap = elaAnalyzer.generateElaHeatmap(img);
        System.out.println("Heatmap generated successfully!");
    }
}