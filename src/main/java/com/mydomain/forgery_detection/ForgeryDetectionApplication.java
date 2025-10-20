package com.mydomain.forgery_detection;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ForgeryDetectionApplication implements CommandLineRunner {

    public static void main(String[] args) {
        // Load OpenCV native library
        try {
            System.load("B:\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
            System.out.println("OpenCV loaded successfully!");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV native library!");
            e.printStackTrace();
            System.exit(1);
        }

        SpringApplication.run(ForgeryDetectionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // For now, nothing to run at startup
        System.out.println("Forgery Detection Application started successfully!");
    }
}