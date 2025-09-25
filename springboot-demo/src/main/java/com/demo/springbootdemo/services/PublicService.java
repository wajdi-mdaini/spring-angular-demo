package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CloudinaryController;
import com.demo.springbootdemo.controller.NotificationController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/public")
public class PublicService {

    private final JwtUtil jwtUtil;
    public PublicService(JwtUtil jwtUtil) {
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
                                                                  HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<User> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
        }else{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userController.getUserByEmail(authentication.getPrincipal().toString());
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
                                                                               @RequestParam("all") boolean isAll,
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
                List<Notification> notifications = isAll ? notificationController.getAllNotificationsByUserTo(user) :
                        notificationController.getLimitedNotificationsByUserTo(user);
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

    @PostMapping(path = "/setnotificationsstatus")
    public ApiResponse<?> setNotificationsReadStatus(@RequestBody List<NotificationDTO> notificationsDTO) {
        ApiResponse<?> response = new ApiResponse<>();
        for(NotificationDTO notificationDTO : notificationsDTO){
            Notification notification = notificationController.getNotificationsById(notificationDTO.getId());
            if(notification == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setShowToast(false);
                break;
            }else{
                notification.setRead(true);
                notificationController.saveNotification(notification);
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setSuccess(true);
            }
        }
        return response;
    }

    @GetMapping(path = "/getnotificationdetails")
    public ApiResponse<Notification> getNotificationDetails(@RequestParam("id") Long id) {
        ApiResponse<Notification> response = new ApiResponse<>();
            Notification notification = notificationController.getNotificationsById(id);
            if(notification == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
            }else{
                response.setData(notification);
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setSuccess(true);
            }
        return response;
    }

    @GetMapping(path = "/checkCurrentPassword")
    public ApiResponse<Boolean> checkCurrentPassword(@RequestParam("password") String password,
                                                     @RequestParam("email") String email) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        User user = userController.login(email,password);
        if(user == null){
            response.setStatus(HttpStatus.OK);
            response.setShowToast(false);
            response.setSuccess(false);
        }else{
            response.setStatus(HttpStatus.OK);
            response.setShowToast(false);
            response.setSuccess(true);
        }
        return response;
    }

    @GetMapping(path = "/teammembers")
    public ApiResponse<TeamDetailsResponse> getTeamMembers(HttpServletRequest request) {
        ApiResponse<TeamDetailsResponse> response = new ApiResponse<>();
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
            if(user == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else{
                TeamDetailsResponse teamDetailsResponse= new TeamDetailsResponse();
                teamDetailsResponse.setTeamManager(user.getTeam().getManager());
                teamDetailsResponse.setMembers(user.getTeam().getMembers());
                response.setData(teamDetailsResponse);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
            }
        }
        return response;
    }

    @PostMapping(path = "/editprofile")
    public ApiResponse<User> editProfile(@RequestBody EditProfileRequest profileRequest,HttpServletRequest request) {
        ApiResponse<User> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else{
            User user = userController.getUserByEmail(profileRequest.getEmail());
            if(user == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else{
                user.setAddress(profileRequest.getAddress());
                user.setCity(profileRequest.getCity());
                user.setCountry(profileRequest.getCountry());
                user.setDateOfBirth(profileRequest.getDateOfBirth().getTime());
                user.setDegree(profileRequest.getDegree());
                user.setFirstname(profileRequest.getFirstname());
                user.setLastname(profileRequest.getLastname());
                user.setPostCode(profileRequest.getPostCode());
                user.setTitle(profileRequest.getTitle());
                response.setData(userController.setUser(user));
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setMessageLabel("manage_profile_edit_profile_done");
            }
        }
        return response;
    }
}
