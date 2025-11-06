package com.demo.springbootdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @JoinColumn(name = "company", nullable = false)
    private Company company;
}
