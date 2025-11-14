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
    private String logoPublicId;
    private String logoURL;

    @OneToOne
    private CompanySettings settings;

    @OneToOne
    @JsonIgnore
    private User companyCreator;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    List<Branding> branding = new ArrayList<>();

    public void setMembers(User member) {
        this.members.add(member);
    }

    public void setBranding(Branding branding) {
        this.branding.add(branding);
    }
}
