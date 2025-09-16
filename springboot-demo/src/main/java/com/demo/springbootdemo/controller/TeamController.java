package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }
    public Team addTeam(Team team) {
        if (team.getName().equals("Administration")) {
            return null;
        }
        return teamRepository.save(team);
    }
}
