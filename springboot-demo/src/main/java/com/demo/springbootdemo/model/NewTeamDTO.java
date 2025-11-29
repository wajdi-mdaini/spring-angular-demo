package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class NewTeamDTO {
    private String teamName;
    private String teamDescription;
    private Long companyId;
    private List<String> memberEmails;
    private String teamManagerEmail;
}
