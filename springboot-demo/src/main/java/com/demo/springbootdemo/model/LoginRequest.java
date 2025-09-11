package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class LoginRequest {
    String email;
    String password;
}
