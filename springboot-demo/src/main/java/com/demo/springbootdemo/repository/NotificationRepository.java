package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTo(User to);
}
