package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Event;
import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private NotificationController notificationController;

    public List<Event> getAllEvents(){
        return eventRepository.findAllByOrderByAtDesc();
    }

    public Event saveEvent(Event event){
        return eventRepository.save(event);
    }

    public Event getEventById(Long idEvent){
        return eventRepository.findById(idEvent).orElse(null);
    }

    public void sendNotification(User userTo, Event event, boolean isEdit){
        Notification notification = new Notification();
        notification.setTo(userTo);
        notification.setFrom(event.getOrganizer());
        notification.setAt(event.getAt());
        if(isEdit){
            notification.setTitleLabel("manage_event_edit_event_notification_title");
            notification.setMessageLabel("manage_event_edit_event_notification_message");
        }else{
            notification.setTitleLabel("manage_event_add_event_notification_title");
            notification.setMessageLabel("manage_event_add_event_notification_message");
        }

        webSocketService.sendNotification(notificationController.saveNotification(notification));
    }

    public void sendDeleteNotification(User userTo, Event event){
        Notification notification = new Notification();
        notification.setTo(userTo);
        notification.setFrom(event.getOrganizer());
        notification.setAt(event.getAt());
        notification.setTitleLabel("manage_event_delete_done_notification_title");
        notification.setMessageLabel("manage_event_delete_done_notification_message");

        webSocketService.sendNotification(notificationController.saveNotification(notification));
    }

    public void deleteEvent(Event event){
        eventRepository.delete(event);
    }
}
