package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.EventController;
import com.demo.springbootdemo.controller.NotificationController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Event;
import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.ApiResponse;
import com.demo.springbootdemo.model.EditEventDTO;
import com.demo.springbootdemo.model.EventDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/event")
public class EventService {

    @Autowired
    private EventController eventController;

    @Autowired
    private UserController userController;

    @Autowired
    private NotificationController notificationController;

    private final JwtUtil jwtUtil;

    public EventService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/getallevents")
    public ApiResponse<List<Event>> getAllEvents(@RequestParam("id") String userEmail, HttpServletRequest request) {
        ApiResponse<List<Event>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User authUser = userController.getUserByEmail(userEmail);
            List<Event> result = new ArrayList<>();
            eventController.getAllEvents().forEach(event -> {
                if (event.getParticipants().contains(authUser)) {
                    result.add(event);
                }
            });
            response.setData(result);
            response.setSuccess(true);
            response.setShowToast(false);
            response.setStatus(HttpStatus.OK);
        }
        return response;
    }

    @PostMapping(path = "/addevent")
    public ApiResponse<Event> addEvent(@RequestBody() EventDTO eventDTO, HttpServletRequest request) {
        ApiResponse<Event> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User eventOrganizer = userController.getUserByEmail(eventDTO.getOrganizerEmail());
            Event event = new Event();
            event.setEventType(eventDTO.getType());
            event.setAt(eventDTO.getAt());
            event.setFrom(eventDTO.getFrom());
            event.setTo(eventDTO.getTo());
            event.setDescription(eventDTO.getDescription());
            event.setTitle(eventDTO.getTitle());
            event.setOrganizer(eventOrganizer);
            for (String participantEmail : eventDTO.getParticipantEmails()) {
                User eventParticipant = userController.getUserByEmail(participantEmail);
                event.setParticipants(eventParticipant);
                if(!event.getOrganizer().getEmail().equals(eventParticipant.getEmail()))
                    eventController.sendNotification(eventParticipant,event,false);
            }
            event = eventController.saveEvent(event);

            eventOrganizer.setEvents(event);
            response.setData(event);
            response.setSuccess(true);
            response.setShowToast(false);
            response.setStatus(HttpStatus.OK);
        }
        return response;
    }

    @GetMapping(path = "/getcompanyusers")
    public ApiResponse<List<User>> getAllCompanyUsers(@RequestParam("id") String userEmail, HttpServletRequest request) {
        ApiResponse<List<User>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User authUser = userController.getUserByEmail(userEmail);
            response.setData(userController.getUsersByCompany(authUser.getCompany()));
            response.setSuccess(true);
            response.setShowToast(false);
            response.setStatus(HttpStatus.OK);
        }
        return response;
    }

    @PostMapping(path = "/editEvent")
    public ApiResponse<Event> editEvent(@RequestBody EditEventDTO editEventDTO, HttpServletRequest request) {
        ApiResponse<Event> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            Event event = eventController.getEventById(editEventDTO.getId());
            if (event == null) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                response.setSuccess(false);
                response.setDoLogout(true);
            } else {
                if (editEventDTO.getTo() == null || editEventDTO.getFrom() == null) {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                    response.setSuccess(false);
                    response.setDoLogout(true);
                } else {
                    if (editEventDTO.isFullcalendarEvent()) {
                        event.setFrom(editEventDTO.getFrom());
                        event.setTo(editEventDTO.getTo());
                        event.setAt(editEventDTO.getAt());
                        event = eventController.saveEvent(event);
                        response.setData(event);
                    } else {
                        event.setEventType(editEventDTO.getType());
                        event.setTitle(editEventDTO.getTitle());
                        event.setDescription(editEventDTO.getDescription());
                        event.setFrom(editEventDTO.getFrom());
                        event.setTo(editEventDTO.getTo());
                        event.setAt(editEventDTO.getAt());
                        event.emptyParticipantList();
                        for (String participantEmail : editEventDTO.getParticipantEmails()) {
                            User eventParticipant = userController.getUserByEmail(participantEmail);
                            event.setParticipants(eventParticipant);
                        }
                        event = eventController.saveEvent(event);
                        response.setData(event);
                    }
                    for(User eventParticipant:  event.getParticipants()){
                        if(!event.getOrganizer().getEmail().equals(eventParticipant.getEmail()))
                            eventController.sendNotification(eventParticipant,event,true);
                    }
                }
                response.setSuccess(true);
                response.setShowToast(false);
                response.setStatus(HttpStatus.OK);
            }

        }
        return response;
    }

    @DeleteMapping(path = "/deletevent")
    public ApiResponse<Event> deleteEvent(@RequestParam("id") Long eventId, HttpServletRequest request) {
        ApiResponse<Event> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            Event event = eventController.getEventById(eventId);
            if (event == null) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                response.setSuccess(false);
                response.setDoLogout(true);
            } else {
                List<String> titlesToDelete = new ArrayList<>();
                titlesToDelete.add("manage_event_edit_event_notification_title");
                titlesToDelete.add("manage_event_add_event_notification_title");
                List<Notification> notificationsToDelete = notificationController.getNotificationListByUserFormAndTitlesLabel(event.getOrganizer(), titlesToDelete);
                notificationsToDelete.forEach(notification -> {
                    notificationController.deleteNotification(notification);
                });
                for(User eventParticipant:  event.getParticipants()){
                    eventController.sendDeleteNotification(eventParticipant,event);
                }
                eventController.deleteEvent(event);
                response.setData(event);
                response.setSuccess(true);
                response.setShowToast(false);
                response.setStatus(HttpStatus.OK);
            }

        }
        return response;
    }
}
