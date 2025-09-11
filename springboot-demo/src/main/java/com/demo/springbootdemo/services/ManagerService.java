package com.demo.springbootdemo.services;

import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/management")
public class ManagerService {

    @Autowired
    private UserController userController;

    @RequestMapping(path = "/add", method = RequestMethod.PUT)
    public User addUser(@RequestBody User user) throws MessagingException {
        userController.addUser(user);
        return user;
    }
}
