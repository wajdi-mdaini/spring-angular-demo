package com.demo.springbootdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Setter @Getter
@Table(name = "`user`")
public class User {
    @Id private String email;
    @JsonIgnore String password;
    private String firstname;
    private String lastname;
    private Long dateOfBirth;
    private Long  creationDate;
    private Long  lastPasswordResetDate;
    private boolean isFirstLogin;
    private boolean locked;
    private String profilePictureUrl;

    private int attempts;
    private String verificationCode;
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "team")
    private Team team;

    @OneToMany(mappedBy = "manager")
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "from")
    @JsonIgnore
    private List<Notification> notificationsFrom = new ArrayList<>();

    @OneToMany(mappedBy = "to")
    @JsonIgnore
    private List<Notification> notificationsTo = new ArrayList<>();
}
