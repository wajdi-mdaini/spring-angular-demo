package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public record NotificationDTO(
            Long id,
            String titleLabel,
            String messageLabel,
            String toName
    ) {}

    public void sendNotification(Notification notification){
        NotificationDTO dto = new NotificationDTO(
                notification.getId(),
                notification.getTitleLabel(),
                notification.getMessageLabel(),
                notification.getTo().getFirstname() + " " + notification.getTo().getLastname()
        );
        messagingTemplate.convertAndSend("/topic/notifications", dto);
    }
}
