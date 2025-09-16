package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
