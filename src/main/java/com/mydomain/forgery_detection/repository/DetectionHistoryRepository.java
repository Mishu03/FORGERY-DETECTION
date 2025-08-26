package com.mydomain.forgery_detection.repository;

import com.mydomain.forgery_detection.model.DetectionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetectionHistoryRepository extends JpaRepository<DetectionHistory, Long> {
}
