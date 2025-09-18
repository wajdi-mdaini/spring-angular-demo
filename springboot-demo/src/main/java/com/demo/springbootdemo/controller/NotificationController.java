package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;


    public Notification saveNotification(Notification notification){
        Notification saved = notificationRepository.save(notification);
        return saved;
    }

    public List<Notification> getNotificationsByUserTo(User user){
        return  this.notificationRepository.findByTo(user);
    }
}
