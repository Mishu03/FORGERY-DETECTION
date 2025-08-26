package com.mydomain.forgery_detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ForgeryDetectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ForgeryDetectionApplication.class, args);
    }
}