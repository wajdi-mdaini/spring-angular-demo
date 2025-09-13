package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter @Getter
public class ChangePasswordRequest {
    private String password;
    private LocalDate lastPasswordResetDate;
}
