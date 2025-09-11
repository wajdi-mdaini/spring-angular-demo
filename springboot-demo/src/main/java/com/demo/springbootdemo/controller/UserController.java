package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.configuration.PasswordGenerator;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserController implements UserDetailsService {

    @Autowired private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private EmailController emailController;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    public User addUser(User user) throws MessagingException {
        String generatedPassword = passwordGenerator.generateStrongPassword();
        user.setFirstLogin(true);
        user.setLocked(false);
        user.setAttempts(0);
        user.setCreationDate(LocalDate.now());
        user.setPassword(passwordEncoder.encode(generatedPassword)); // hash the password
        emailController.sendWelcomePasswordEmail(
                user.getEmail(),
                user.getFirstname() + " " + user.getLastname(),
                user.getEmail(),
                generatedPassword,
                user.getCompany().getName()
        );
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if(matches){
            return user;
        }
        return null;
    }
}

