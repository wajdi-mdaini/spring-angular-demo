package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CompanyController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.ApiResponse;
import com.demo.springbootdemo.model.EditTeamRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @RequestMapping(path = "/add", method = RequestMethod.PUT)
//    public User addUser(@RequestBody User user) throws MessagingException {
//        userController.addUser(user);
//        return user;
//    }

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
            response.setSuccess(true);
            response.setShowToast(false);
            response.setStatus(HttpStatus.OK);
            response.setData(companyController.getTeams(company));
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

}
