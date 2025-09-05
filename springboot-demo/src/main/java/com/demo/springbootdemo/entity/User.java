package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
public class User {
    @Id private String email;
    private String password;
    private String firstname;
    private String lastname;
    private boolean isFirstLogin;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "manager_email")
    private User manager;   // who is my manager?

    @OneToMany(mappedBy = "manager")
    private List<User> employees = new ArrayList<>();
}
