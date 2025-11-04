package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String url;
    private Long at;
    private String size;
    private String type;
    private String cloudId;

    @ManyToOne
    private User to;

    @ManyToOne
    private User from;
}
