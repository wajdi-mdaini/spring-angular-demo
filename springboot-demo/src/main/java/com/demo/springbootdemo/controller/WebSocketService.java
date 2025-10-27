package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.model.NotificationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(Notification notification){
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setAt(notification.getAt());
        dto.setRead(notification.isRead());
        dto.setMessageLabel(notification.getMessageLabel());
        dto.setTitleLabel(notification.getTitleLabel());
        dto.setFromName(notification.getFrom().getFirstname() + " " + notification.getFrom().getLastname());
        dto.setFromId(notification.getFrom().getEmail());
        dto.setFromProfilePictureUrl(notification.getFrom().getProfilePictureUrl());
        dto.setToEmail(notification.getTo().getEmail());

        messagingTemplate.convertAndSend("/topic/notifications", dto);
    }
}
