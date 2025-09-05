package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserService {

    private final JwtUtil jwtUtil;

    public UserService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired private UserController userController;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(@RequestBody User user) {
        if(userController.login(user.getEmail(),user.getPassword()) != null){
            return jwtUtil.generateToken(user.getEmail());
        }
        return null;
    }
    @RequestMapping(path = "/add", method = RequestMethod.PUT)
    public User addUser(@RequestBody User user) {
        userController.addUser(user);
        return user;
    }
}
