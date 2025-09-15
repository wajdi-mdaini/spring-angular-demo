package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CompanyController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Role;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.token.expiration}")
    private int EXPIRATION;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
                                                            HttpServletResponse httpResponse) {
        ApiResponse<LoginResponse> response = new ApiResponse<>();
        User loggedInUser = userController.login(loginRequest.getEmail(),loginRequest.getPassword());
        if( loggedInUser != null){
            if(loggedInUser.isLocked()) {
                response.setData(null);
                response.setStatus(HttpStatus.LOCKED);
                response.setSuccess(false);
                response.setMessageLabel("auth_signin_blocked_user_error_message");
            }else{
                LoginResponse  loginResponse = new LoginResponse();
                loginResponse.setUser(loggedInUser);
                String jwtToken = jwtUtil.generateToken(loggedInUser.getEmail());
                response.setData(loginResponse);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
                Cookie cookie = new Cookie("jwt", jwtToken);
                cookie.setHttpOnly(true); // protect from JavaScript
                cookie.setSecure(true);   // only HTTPS
                cookie.setPath("/");      // available for the whole domain
                cookie.setMaxAge(EXPIRATION * 60); // 30 minutes for expiration
                httpResponse.addCookie(cookie);

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

    @GetMapping("/login/check")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete cookie
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<LoginResponse>> getUserProfile(HttpServletRequest request) {
        ApiResponse<LoginResponse> response = new ApiResponse<>();
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
            LoginResponse  loginResponse = new LoginResponse();
            loginResponse.setUser(user);
            response.setData(loginResponse);
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            response.setShowToast(false);
        }
        return new ResponseEntity<>( response , response.getStatus());
    }
}
