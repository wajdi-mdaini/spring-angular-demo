package com.demo.springbootdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore String password;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private LocalDate  creationDate;
    private LocalDate  lastPasswordResetDate;
    private boolean isFirstLogin;
    private boolean locked;
    private int attempts;
    private String verificationCode;
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
