package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class EditTeamRequest {
    private List<String> remainingUsers;
    private List<String> teamMembers;
    private String managerEmail;
    private Long teamId;
    private String description;
    private String name;

}
