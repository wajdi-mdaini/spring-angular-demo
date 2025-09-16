package com.demo.springbootdemo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryController {

    private final Cloudinary cloudinary;

    public CloudinaryController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadProfilePicture(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "users profile pictures"));
        return uploadResult.get("secure_url").toString();
    }

    public String uploadCompanyLogo(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "companies logo"));
        return uploadResult.get("secure_url").toString();
    }
}
