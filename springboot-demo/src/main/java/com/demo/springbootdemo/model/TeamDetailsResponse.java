package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class TeamDetailsResponse {

    private User teamManager;
    private List<User> members;
}
