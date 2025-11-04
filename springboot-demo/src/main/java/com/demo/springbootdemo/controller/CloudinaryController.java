package com.demo.springbootdemo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FilenameUtils;
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

    public Map<String, String> uploadProfilePicture(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "users profile pictures"));
        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        return Map.of(
                "url", url,
                "publicId", publicId
        );
    }

    public Map<String, String> uploadDocument(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "users documents",
                                          "resource_type", "raw",
                                          "public_id", FilenameUtils.removeExtension(originalFilename)));
        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        return Map.of(
                "url", url,
                "publicId", publicId
        );
    }


    public Map<String, String> uploadCompanyLogo(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "companies logo"));
        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        return Map.of(
                "url", url,
                "publicId", publicId
        );
    }

    public void deleteFile(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) return;
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public void deleteDocumentFile(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            System.out.println("❌ Skipping deletion: publicId is null or empty");
            return;
        }
        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));

        if (!"ok".equals(result.get("result"))) {
            System.out.println("⚠️ Could not delete Cloudinary file: " + result);
        }
    }
}
