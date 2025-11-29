package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class CompanySummary {
    private Company company;
    private User companyCreator;
    private Map<String, Integer> userNumbers = new HashMap<>();
    private User teamManager;
    private List<User> teamMembers = new ArrayList<>();
    private List<Document> lastDocuments = new ArrayList<>();
    private List<Event> lastEvents = new ArrayList<>();
    private Map<String, Integer> eventNumbers = new HashMap<>();
    private Team team;

}
