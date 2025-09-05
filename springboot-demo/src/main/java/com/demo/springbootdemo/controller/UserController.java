package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.configuration.PasswordConfig;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
public class UserController implements UserDetailsService {
    @Autowired private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    @Autowired
    private PasswordConfig passwordConfig;

    public void addUser(User user) {
        user.setFirstLogin(true);
        user.setPassword(passwordConfig.passwordEncoder().encode(user.getPassword())); // hash the password
        userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user != null && passwordConfig.passwordEncoder().matches(password, user.getPassword())){
            return user;
        }
        return null;
    }
}

