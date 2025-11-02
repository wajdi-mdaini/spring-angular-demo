package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {
    private String firstname;
    private String email;
    private String lastname;
    private Long dateOfBirth;
    private String address;
    private String city;
    private String country;
    private String postCode;
    private String degree;
    private String title;
    private Long teamId;
}
