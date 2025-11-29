package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${app.max.notifications.number}")
    private int maxNotificationsNumber;


    public Notification saveNotification(Notification notification){
        Notification saved = notificationRepository.save(notification);
        return saved;
    }

    public List<Notification> getLimitedNotificationsByUserTo(User user){
        List<Notification> savedNotifications = this.notificationRepository.findByToOrderByAtDesc(user);
        List<Notification> notifications = new ArrayList<>();
        int numberOfNotifications = Math.min(savedNotifications.size(), maxNotificationsNumber);
        for(int index = 0; index < numberOfNotifications; index++){
            notifications.add(savedNotifications.get(index));
        }
        return  notifications;
    }

    public List<Notification> getAllNotificationsByUserTo(User user){
        return this.notificationRepository.findByToOrderByAtDesc(user);
    }

    public List<Notification> getAllNotificationsByUserFrom(User user){
        return this.notificationRepository.findByFrom(user)
                .stream()
                .sorted(Comparator.comparing(Notification::getAt).reversed())
                .toList();
    }

    public List<Notification> getNotificationListByUserFormAndTitlesLabel(User userFrom,List<String> titles){
        return this.notificationRepository.findByFromAndTitleLabelIn(userFrom, titles);
    }

    public Notification getNotificationsById(Long idNotification){
        return notificationRepository.findById(idNotification)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    public void deleteNotification(Notification notification){
        notificationRepository.delete(notification);
    }
}
