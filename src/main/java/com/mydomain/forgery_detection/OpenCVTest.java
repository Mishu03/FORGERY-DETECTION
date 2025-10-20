package com.mydomain.forgery_detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class OpenCVTest {
    static {
        System.load("C:\\Users\\msnai\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
    }

    public static void main(String[] args) {
        String path = "A:\\MJR\\forgery-detection\\test-data\\image.jpg";
        Mat img = Imgcodecs.imread(path);

        if (img.empty()) {
            System.out.println("Failed to read image! Check path or format.");
        } else {
            System.out.println("Image read successfully!");
            System.out.println("Cols: " + img.cols() + ", Rows: " + img.rows() + ", Channels: " + img.channels());
        }
    }
}