package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.Role;
import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.TeamRepository;
import com.demo.springbootdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TeamController {

    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private NotificationController notificationController;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<User> getTeamMembers(Long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        return team.get().getMembers();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<Team> getTeamByManager(User manager) {
        return this.teamRepository.findByManager(manager);
    }

    public Team getTeamById(Long teamId) {
        return this.teamRepository.findById(teamId).get();
    }

    public Team deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId).get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authUser = getUserByEmail(authentication.getPrincipal().toString());
        for (User user : team.getMembers()) {
            user.setTeam(null);
            if(user.getTeams().isEmpty())
                user.setRole(Role.EMPLOYEE);
            userRepository.save(user);

            Notification notification = new Notification();
            notification.setTo(user);
            notification.setFrom(authUser);
            notification.setAt(new Date().getTime());
            notification.setTitleLabel("manage_teams_delete_team_notification_title");
            notification.setMessageLabel("manage_teams_delete_team_notification_message");
            webSocketService.sendNotification(notificationController.saveNotification(notification));
        }
        teamRepository.delete(team);

        return team;
    }
}
