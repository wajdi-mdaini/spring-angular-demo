package com.demo.springbootdemo.services;

import com.demo.springbootdemo.controller.CloudinaryController;
import com.demo.springbootdemo.controller.UserController;
import com.demo.springbootdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/employee")
public class EmployeeService {

    @Autowired
    private UserController userController;

    @Autowired
    private CloudinaryController cloudinaryController;

    @PostMapping("/upload-profile")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("email") String email) {

        try {
            String url = cloudinaryController.uploadProfilePicture(file);

            User user = userController.getUserByEmail(email);

            user.setProfilePictureUrl(url);
            userController.setUser(user);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

}
