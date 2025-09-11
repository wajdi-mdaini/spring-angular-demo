package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class Branding {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String label;
    private String value;

    @ManyToOne()
    @JoinColumn(name = "company", nullable = false)
    private Company company;
}
