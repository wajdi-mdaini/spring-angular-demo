package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String message;
    private boolean read;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;
}
