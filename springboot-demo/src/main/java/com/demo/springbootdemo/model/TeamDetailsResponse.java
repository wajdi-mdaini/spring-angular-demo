package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class TeamDetailsResponse {
    private Team team;
    private List<User> members;
    private User manager;
}
