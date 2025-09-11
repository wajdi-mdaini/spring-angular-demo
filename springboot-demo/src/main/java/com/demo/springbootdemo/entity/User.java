package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Setter @Getter
public class User {
    @Id private String email;
    private String password;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private LocalDate  creationDate;
    private boolean isFirstLogin;
    private boolean locked;
    private int attempts;
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "manager_email")
    private User manager;   // who is my manager?

    @OneToMany(mappedBy = "manager")
    private List<User> employees = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company", nullable = false)
    private Company company;   // what is my company?

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();
}
