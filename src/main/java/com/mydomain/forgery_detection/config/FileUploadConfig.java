package com.mydomain.forgery_detection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        
        // 100MB max file size
        resolver.setMaxUploadSize(100 * 1024 * 1024);
        resolver.setMaxInMemorySize(1024 * 1024);
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);
        
        return resolver;
    }
}
