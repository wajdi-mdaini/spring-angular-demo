package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CompanyController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Role;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.LoginRequest;
import com.demo.springbootdemo.model.LoginResponse;
import com.demo.springbootdemo.model.SharedSettings;
import com.demo.springbootdemo.model.SignUpRequest;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
public class LoginService {

    private final JwtUtil jwtUtil;
    public LoginService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    private UserController userController;

    @Autowired
    private CompanyController companyController;

    @Autowired
    private SharedSettings sharedSettings;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        User loggedInUser = userController.login(loginRequest.getEmail(),loginRequest.getPassword());
        if( loggedInUser != null){
            LoginResponse  loginResponse = new LoginResponse();
            loginResponse.setUser(loggedInUser);
            loginResponse.setToken(jwtUtil.generateToken(loggedInUser.getEmail()));
            return loginResponse;
        }
        return new LoginResponse();
    }

    @RequestMapping(path = "/signup", method = RequestMethod.PUT)
    public User signup(@RequestBody SignUpRequest signUpRequest) throws MessagingException {
        Company company = companyController.addCompany(signUpRequest.getCompany());
        signUpRequest.getUser().setRole(Role.ADMIN);
        signUpRequest.getUser().setCompany(company);
        return userController.addUser(signUpRequest.getUser());
    }

    @RequestMapping(path = "/settings", method = RequestMethod.GET)
    public SharedSettings getSharedSettings() {
        return sharedSettings;
    }
}
