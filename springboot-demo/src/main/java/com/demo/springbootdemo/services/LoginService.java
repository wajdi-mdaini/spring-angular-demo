package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CompanyController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Role;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> response = new ApiResponse<>();
        User loggedInUser = userController.login(loginRequest.getEmail(),loginRequest.getPassword());
        if( loggedInUser != null){
            LoginResponse  loginResponse = new LoginResponse();
            loginResponse.setUser(loggedInUser);
            if(loggedInUser.isLocked()) {
                response.setData(null);
                response.setStatus(HttpStatus.LOCKED);
                response.setSuccess(false);
                response.setMessageLabel("auth_signin_blocked_user_error_message");
            }else{
                loginResponse.setToken(jwtUtil.generateToken(loggedInUser.getEmail()));
                response.setData(loginResponse);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
            }

        }else {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setShowToast(false);
        }
        return new ResponseEntity<>( response , response.getStatus());
    }

    @RequestMapping(path = "/signup", method = RequestMethod.PUT)
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignUpRequest signUpRequest) throws MessagingException {
        ApiResponse<User> response = new ApiResponse<>();
        Company company = companyController.createCompany(signUpRequest.getCompany());
        if(company == null){
            response.setStatus(HttpStatus.CONFLICT);
            response.setMessageLabel("auth_signup_used_company_name_error_message");
            response.setData(null);
            response.setSuccess(false);
        }else {
            signUpRequest.getUser().setRole(Role.ADMIN);
            signUpRequest.getUser().setCompany(company);
            response = userController.addUser(signUpRequest.getUser());
        }
        return new ResponseEntity<>( response , response.getStatus());
    }

    @RequestMapping(path = "/resetpasswordconfirmation", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<String>> resetPasswordConfirmation(@RequestParam("email") String email) throws MessagingException {
        ApiResponse<String> response = userController.resetPasswordMailConfirmation(email);
        return new ResponseEntity<>( response , response.getStatus());
    }

    @RequestMapping(path = "/resetpasswordcodecheck", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<Boolean>>  resetPasswordCodeCheck(@RequestParam("code") String code) {
        ApiResponse<Boolean> response = userController.resetPasswordCodeCheck(code);
        return new ResponseEntity<>( response , response.getStatus());
    }

    @RequestMapping(path = "/changepassword", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<Boolean>> ChangePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ApiResponse<Boolean> response = userController.changePassword(changePasswordRequest);
        return new ResponseEntity<>( response , response.getStatus());
    }

    @RequestMapping(path = "/settings", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<SharedSettings>> getSharedSettings() {
        ApiResponse<SharedSettings> response = new ApiResponse<>();
        response.setData(sharedSettings);
        response.setStatus(HttpStatus.OK);
        response.setSuccess(true);
        response.setShowToast(false);
        return new ResponseEntity<>( response , response.getStatus());
    }
}
