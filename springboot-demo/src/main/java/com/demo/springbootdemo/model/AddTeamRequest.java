package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class AddTeamRequest {
    private List<String> memberEmails;
    private Team team;
    private String managerEmail;
}
