package com.forensic.detection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        
        // Set maximum file size (100MB)
        resolver.setMaxUploadSize(100 * 1024 * 1024);
        
        // Set maximum in-memory size (1MB)
        resolver.setMaxInMemorySize(1024 * 1024);
        
        // Set default encoding
        resolver.setDefaultEncoding("UTF-8");
        
        // Resolve Lazily for better performance
        resolver.setResolveLazily(true);
        
        return resolver;
    }
}
