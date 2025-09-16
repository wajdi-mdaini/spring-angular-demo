package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titleLabel;
    private String messageLabel;
    private boolean read = false;
    @Temporal(TemporalType.TIMESTAMP)
    private Long at;

    @ManyToOne
    @JoinColumn(name = "from", nullable = false)
    private User from;

    @ManyToOne
    @JoinColumn(name = "to", nullable = false)
    private User to;
}
