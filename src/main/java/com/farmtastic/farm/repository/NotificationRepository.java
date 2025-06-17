package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 최신순으로 알림 목록 조회
    List<Notification> findAllByOrderByCreatedAtDesc();

}
