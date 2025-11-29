package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Document;
import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.print.Doc;
import java.util.Date;
import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private NotificationController notificationController;

    public List<Document> findByTo(User user) {
        return documentRepository.findByToOrderByAtDesc(user);
    }

    public List<Document> findByFrom(User user) {
        return documentRepository.findByFromOrderByAtDesc(user);
    }

    public void deleteDocument(Document document) {
        documentRepository.delete(document);
    }

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    public void sendNotification(Document document, User userFrom, User userTo) {
        Notification notification = new Notification();
        notification.setTo(userTo);
        notification.setFrom(userFrom);
        notification.setAt(document.getAt());
        notification.setTitleLabel("manage_document_add_document_notification_title");
        notification.setMessageLabel("manage_document_add_document_notification_message");
        webSocketService.sendNotification(notificationController.saveNotification(notification));
    }

    public void sendEditDocumentNotification(Document document, User userFrom, User userTo) {
        Notification notification = new Notification();
        notification.setTo(userTo);
        notification.setFrom(userFrom);
        notification.setAt(document.getAt());
        notification.setTitleLabel("manage_document_edit_document_notification_title");
        notification.setMessageLabel("manage_document_edit_document_notification_message");
        webSocketService.sendNotification(notificationController.saveNotification(notification));
    }

    public Document findById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }
}
