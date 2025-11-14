package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class Holiday {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long at;
    private Long from;
    private Long to;
    @Enumerated(EnumType.STRING)
    private HolidayType type;
    @Enumerated(EnumType.STRING)
    private HolidayStatus status;

    @ManyToOne
    private User user;
}
