package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class EditProfileRequest {
    String address;
    String city;
    String country;
    Date dateOfBirth;
    String email;
    String degree;
    String firstname;
    String lastname;
    String postCode;
    String title;
}
