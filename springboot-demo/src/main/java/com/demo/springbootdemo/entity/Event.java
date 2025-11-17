package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long at;
    private Long from;
    private Long to;

    @Enumerated(EnumType.STRING)
    EventType eventType;

    @ManyToMany
    private List<User> participants = new ArrayList<>();

    @ManyToOne
    private User organizer;

    public void setParticipants(User participant) {
        this.participants.add(participant);
    }
}
