package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.EventType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EditEventDTO {
    private Long id;
    private String title;
    private String description;
    private Long at;
    private Long from;
    private Long to;
    private EventType type;
    private List<String> participantEmails = new ArrayList<>();
    private boolean fullcalendarEvent;
}
