package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findById(Long id);
    List<Notification> findByTo(User to);
    List<Notification> findByFrom(User to);
    List<Notification> findByFromAndTitleLabelIn(User userFrom, List<String> titles);
}
