package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CloudinaryController;
import com.demo.springbootdemo.controller.NotificationController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.ApiResponse;
import com.demo.springbootdemo.model.NotificationDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/employee")
public class EmployeeService {

    private final JwtUtil jwtUtil;
    public EmployeeService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Autowired
    private UserController userController;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private CloudinaryController cloudinaryController;

    @PostMapping("/upload-profile")
    public ResponseEntity<ApiResponse<User>> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                                  @RequestParam("email") String email,
                                                                  HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<User> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
        }else{
            User user = userController.getUserByEmail(email);
            if(user == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else {
                try {
                    Map<String, String> uploadResponse = cloudinaryController.uploadProfilePicture(file);
                    String pictureUrl = uploadResponse.get("url");
                    String pictureId = uploadResponse.get("publicId");
                    if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty() ) cloudinaryController.deleteFile(user.getProfilePicturePublicId());
                    user.setProfilePictureUrl(pictureUrl);
                    user.setProfilePicturePublicId(pictureId);
                    user = userController.setUser(user);
                    response.setStatus(HttpStatus.OK);
                    response.setMessageLabel("upload_profile_picture_success");
                    response.setData(user);
                    response.setSuccess(true);

                } catch (Exception e) {
                    response.setData(null);
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    response.setSuccess(false);
                    response.setMessageLabel("upload_profile_picture_failed");
                }
            }
        }
        return new ResponseEntity<>( response , response.getStatus());
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(@RequestParam("email") String email,
                                                                               HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<List<NotificationDTO>> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
        }else{
            User user = userController.getUserByEmail(email);
            if(user == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else {
                List<Notification> notifications = notificationController.getNotificationsByUserTo(user);
                List<NotificationDTO> dtoList = new ArrayList<>();
                for(Notification notification : notifications){
                    NotificationDTO dto = new NotificationDTO();
                    dto.setId(notification.getId());
                    dto.setAt(notification.getAt());
                    dto.setRead(notification.isRead());
                    dto.setMessageLabel(notification.getMessageLabel());
                    dto.setTitleLabel(notification.getTitleLabel());
                    dto.setFromName(notification.getFrom().getFirstname() + " " + notification.getFrom().getLastname());
                    dto.setFromId(notification.getFrom().getEmail());
                    dto.setFromProfilePictureUrl(notification.getFrom().getProfilePictureUrl());
                    dtoList.add(dto);
                }
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setData(dtoList);
                response.setSuccess(true);
            }
        }
        return new ResponseEntity<>( response , response.getStatus());
    }
}
