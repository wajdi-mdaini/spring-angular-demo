package com.demo.springbootdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<User> members = new ArrayList<>();
    @ManyToOne
    @JsonIgnore
    private User manager;
    @ManyToOne
    private Company company;

    public void setMembers(User member) {
        this.members.add(member);
    }
}
