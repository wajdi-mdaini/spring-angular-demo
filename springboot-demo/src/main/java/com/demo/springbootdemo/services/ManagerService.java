package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CompanyController;
import com.demo.springbootdemo.controller.TeamController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Role;
import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/management")
public class ManagerService {

    private final JwtUtil jwtUtil;
    public ManagerService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    private CompanyController companyController;

    @Autowired
    private UserController userController;

    @Autowired
    private TeamController teamController;

    @GetMapping(path = "/getallemployees")
    public ApiResponse<List<User>> getAllEmployees( HttpServletRequest request){
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<List<User>> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else {
            response.setData(userController.getAllEmployees());
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            response.setShowToast(false);
        }

        return response;
    }

    @PostMapping(path = "/getteams")
    public ResponseEntity<ApiResponse<List<Team>>> getCompanyTeams(@RequestBody Company company,
                                                                   HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<List<Team>> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else {
            String email = jwtUtil.extractUsername(token);
            User user = userController.getUserByEmail(email);
            company = companyController.getCompanyById(company.getId());
            response.setData(companyController.getTeams(company,user));
            response.setSuccess(true);
            response.setShowToast(false);
            response.setStatus(HttpStatus.OK);
        }
        return new ResponseEntity<>( response , response.getStatus());
    }

    @PutMapping(path="/editteam")
    public ResponseEntity<ApiResponse<Team>> editTeam(@RequestBody EditTeamRequest editTeamRequest, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<Team> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else {
            User manager = userController.getUserByEmail(editTeamRequest.getManagerEmail());
            if(!manager.getRole().equals(Role.ADMIN)){
                manager.setRole(Role.MANAGER);
            }
            manager = userController.save(manager);
            Team team = editTeamRequest.getTeam();
            team.setManager(manager);
            team = companyController.saveTeam(team);
            team = userController.emptyUserTeam(editTeamRequest.getRemainingUsers(),team);
            userController.setUserTeam(editTeamRequest.getTeamMembers(),team);
            response.setSuccess(true);
            response.setMessageLabel("manage_teams_edit_done");
            response.setStatus(HttpStatus.OK);
            response.setData(team);
        }
        return new ResponseEntity<>( response , response.getStatus());
    }

    @GetMapping(path = "/teammembers")
    public ApiResponse<List<User>> getTeamMembers(@RequestParam("id")Long idTeam, HttpServletRequest request) {
        ApiResponse<List<User>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else{
            String email = jwtUtil.extractUsername(token);
            User user = userController.getUserByEmail(email);
            if(user == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else{
                List<User> users = teamController.getTeamMembers(idTeam);
                response.setData(users);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
            }
        }
        return response;
    }

    @PutMapping(path = "/addteam")
    public ApiResponse<Team> addTeam(@RequestBody() AddTeamRequest addTeamRequest, HttpServletRequest request) {
        ApiResponse<Team> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else{
            String email = jwtUtil.extractUsername(token);
            User user = userController.getUserByEmail(email);
            if(user == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else{
                User manager = userController.getUserByEmail(addTeamRequest.getManagerEmail());
                if(!manager.getRole().equals(Role.ADMIN)){
                    manager.setRole(Role.MANAGER);
                }
                manager = userController.save(manager);
                Team team = addTeamRequest.getTeam();
                team.setManager(manager);

                team = companyController.saveTeam(team);
                userController.addUsersToTeam(addTeamRequest.getMemberEmails(),team);
                response.setData(team);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setMessageLabel("manage_teams_add_done");
            }
        }
        return response;
    }

    @GetMapping(path = "/getmanagerlist")
    public ApiResponse<List<User>> GetManagerList(@RequestParam("id") Long idCompany, HttpServletRequest request) {
        ApiResponse<List<User>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else{
            Company company = companyController.getCompanyById(idCompany);
            List<User> users = new ArrayList<>();
            company.getMembers().forEach(member -> {
                if(member.getRole().equals(Role.MANAGER))
                    users.add(member);
            });
            response.setData(users);
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            response.setShowToast(false);
        }
        return response;
    }

    @DeleteMapping(path = "/deleteteam")
    public ApiResponse<Team> deleteTeam(@RequestParam("id") Long teamId, HttpServletRequest request) {
        ApiResponse<Team> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else{
            String email = jwtUtil.extractUsername(token);
            User user = userController.getUserByEmail(email);
            if(user == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else{
               Team team = teamController.deleteTeam(teamId);
                response.setData(team);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setMessageLabel("manage_teams_delete_team_success_deleting_message");
            }
        }
        return response;
    }

    @PostMapping(path = "/getUsers")
    public ApiResponse<List<User>> getUsers(@RequestBody GetUsersRequest getUsersRequest, HttpServletRequest request) {
        ApiResponse<List<User>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else{
            Company company = companyController.getCompanyById(getUsersRequest.getCompany().getId());
            List<User> users = new ArrayList<>();
            if(getUsersRequest.getUser().getRole().equals(Role.MANAGER)){
                List<Team> teams = teamController.getTeamById(getUsersRequest.getUser());
                for(Team team : teams){
                    users.addAll(team.getMembers());
                }
            } else if(getUsersRequest.getUser().getRole().equals(Role.ADMIN)){
                users.addAll(company.getMembers());
            }
            response.setData(users);
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            response.setMessageLabel("manage_teams_delete_team_success_deleting_message");

        }
        return response;
    }

    }
