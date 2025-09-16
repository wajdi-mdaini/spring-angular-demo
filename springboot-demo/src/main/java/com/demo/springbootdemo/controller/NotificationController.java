package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification saveNotification(Notification notification){
        return notificationRepository.save(notification);
    }
}
