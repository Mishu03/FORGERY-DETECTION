package com.mydomain.forgery_detection;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PythonServiceLauncher implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        new ProcessBuilder("python", "A:/MJR/forgery-detection/ml-image-detector/app.py")
                .inheritIO()
                .start();
        System.out.println("Python ML service launched.");
    }
}