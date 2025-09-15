package com.demo.springbootdemo.model;


import com.demo.springbootdemo.entity.User;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class LoginResponse {
    User user;
}
