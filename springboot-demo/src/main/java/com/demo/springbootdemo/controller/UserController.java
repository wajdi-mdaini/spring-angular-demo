package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.configuration.PasswordGenerator;
import com.demo.springbootdemo.configuration.SchedulerConfig;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.ApiResponse;
import com.demo.springbootdemo.model.ChangePasswordRequest;
import com.demo.springbootdemo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class UserController implements UserDetailsService {

    @Autowired private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private EmailController emailController;

    @Value("${app.shared.verification-code-expire-in}")
    private long verificationExpireIn;

    @Value("${app.max.login.attempts}")
    private long maxLoginAttempts;

    private ScheduledFuture<?> futureTask;

    private final ThreadPoolTaskScheduler scheduler;

    private User authUser;

    public UserController(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        return new CustomUserDetails(user);
    }

    public ApiResponse<User> addUser(User user) throws MessagingException {
        ApiResponse<User> response = new ApiResponse<>();
        User existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser != null){
            response.setStatus(HttpStatus.CONFLICT);
            response.setMessageLabel("auth_signup_used_email_error_message");
            response.setSuccess(false);
            response.setData(null);
            return response;
        }else {
            String generatedPassword = passwordGenerator.generateStrongPassword();
            user.setFirstLogin(true);
            user.setLocked(false);
            user.setAttempts(0);
            user.setCreationDate(LocalDate.now());
            user.setPassword(passwordEncoder.encode(generatedPassword)); // hash the password
            emailController.sendWelcomePasswordEmail(
                    user.getFirstname() + " " + user.getLastname(),
                    user.getEmail(),
                    generatedPassword,
                    user.getCompany().getName()
            );
            userRepository.save(user);
            response.setStatus(HttpStatus.OK);
            response.setMessageLabel("auth_signup_success_message");
            response.setData(user);
            response.setSuccess(true);
            return response;
        }
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user != null){
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            if (matches) {
                return user;
            } else {
                user.setAttempts(user.getAttempts() + 1);
                if (user.getAttempts() > maxLoginAttempts) user.setLocked(true);
                userRepository.save(user);
            }
        }
        if(user != null && user.isLocked()) return user;
        return null;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void scheduleAttributeChange(User user) {
        futureTask = scheduler.schedule(
                () -> {
                    user.setVerificationCode("");
                    userRepository.save(user);
                    System.out.println("Verification code expired");
                },
                new java.util.Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(verificationExpireIn))
        );
    }

    public ApiResponse<String> resetPasswordMailConfirmation(String email) throws MessagingException {
        ApiResponse<String> response = new ApiResponse<>();
        User user = getUserByEmail(email);
        if(user == null){
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setMessageLabel("error_status_UNAUTHORIZED");
            response.setSuccess(false);
        }else{
            String code = passwordGenerator.generateCode();
            user.setVerificationCode(code);
            userRepository.save(user);
            authUser = user;
            scheduleAttributeChange(user);
            emailController.sendResetPasswordConfirmationEmail(email, code, user, verificationExpireIn);
            response.setStatus(HttpStatus.OK);
            response.setData(user.getFirstname() + " " + user.getLastname());
            response.setShowToast(false);
            response.setSuccess(true);
        }
        return response;
    }

    public ApiResponse<Boolean> resetPasswordCodeCheck(String code) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        if(authUser == null){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
            response.setSuccess(false);
            response.setDoLogout(true);
        }else{
            boolean status = authUser.getVerificationCode().equalsIgnoreCase(code);
            if(status){
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setSuccess(true);
            }else{
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setShowToast(false);
                response.setSuccess(false);
            }

            response.setSuccess(status);
        }
        return response;
    }

    public ApiResponse<Boolean> changePassword(ChangePasswordRequest changePasswordRequest) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        if(authUser == null){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
            response.setSuccess(false);
            response.setDoLogout(true);
        }else{
            authUser.setPassword(changePasswordRequest.getPassword());
            authUser.setLocked(false);
            authUser.setAttempts(0);
            authUser.setLastPasswordResetDate(LocalDate.now());
            authUser.setFirstLogin(false);
            userRepository.save(authUser);
            response.setStatus(HttpStatus.OK);
            response.setMessageLabel("auth_forget_password_success_message");
            response.setSuccess(true);
        }
        return response;
    }

    @PostConstruct
    public void emptyAllVerificationCodes() {
        userRepository.findAll().forEach(user -> {
            user.setVerificationCode("");
            userRepository.save(user);
        });
    }

}

