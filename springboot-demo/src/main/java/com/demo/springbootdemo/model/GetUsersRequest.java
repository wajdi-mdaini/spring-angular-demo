package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class GetUsersRequest {
    private String userEmail;
    private Long companyId;
}
