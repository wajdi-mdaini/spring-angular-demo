package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Event;
import com.demo.springbootdemo.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents(){
        return eventRepository.findAllByOrderByAtDesc();
    }

    public Event saveEvent(Event event){
        return eventRepository.save(event);
    }

    public Event getEventById(Long idEvent){
        return eventRepository.findById(idEvent).orElse(null);
    }
}
