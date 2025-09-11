package com.demo.springbootdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String address;
    private String email;
    private Long phone;
    private String website;
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    List<Branding> branding = new ArrayList<>();
}
