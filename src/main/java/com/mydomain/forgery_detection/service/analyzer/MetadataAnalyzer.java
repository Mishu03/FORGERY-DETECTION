package com.mydomain.forgery_detection.service.analyzer;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.*;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class MetadataAnalyzer {
    public double analyze(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(is);
            // Check key EXIF directories
            ExifIFD0Directory exifDir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            ExifSubIFDDirectory subExifDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDir == null && subExifDir == null) {
                return 0.2; // very suspicious: no EXIF data
            }
            double score = 0.0;
            // Check camera model
            if (exifDir != null && exifDir.containsTag(ExifIFD0Directory.TAG_MODEL)) {
                score += 0.5;
            }
            // Check capture date
            if (subExifDir != null && subExifDir.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) {
                score += 0.5;
            }
            return score; // 0–1, 1 means consistent metadata
        } catch (Exception e) {
            // if reading metadata fails, suspicious
            return 0.0;
        }
    }
}
