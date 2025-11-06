package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.BrandingController;
import com.demo.springbootdemo.controller.CloudinaryController;
import com.demo.springbootdemo.controller.CompanyController;
import com.demo.springbootdemo.entity.Branding;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.ApiResponse;
import com.demo.springbootdemo.model.CompanyDTO;
import com.demo.springbootdemo.model.SetBrandingResponse;
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
@RequestMapping(path = "/company")
public class CompanyService {

    private final JwtUtil jwtUtil;
    public CompanyService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Autowired
    private CompanyController companyController;

    @Autowired
    private BrandingController brandingController;

    @Autowired
    private CloudinaryController cloudinaryController;

    @PostMapping(path = "/setcompanydetails")
    public ResponseEntity<ApiResponse<Company>> setCompanyDetails(@RequestBody CompanyDTO companyDTO,
                                                                  HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<Company> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else {
            Company company = companyController.getCompanyById(companyDTO.getCompanyId());
            company.setName(companyDTO.getCompanyName());
            company.setEmail(companyDTO.getCompanyEmail());
            company.setDescription(companyDTO.getDescription());
            company.setPhone(companyDTO.getCompanyPhone());
            company.setWebsite(companyDTO.getCompanyWebLink());
            company.setAddress(companyDTO.getCompanyAddress());
            response = companyController.setCompany(company);
        }
        return new ResponseEntity<>( response , response.getStatus());
    }

    @PutMapping(path = "/brandingsetup")
    public ApiResponse<Company> setBranding(@RequestParam("id") Long companyId, @RequestBody List<Branding> brandingList, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<Company> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else {
            Company company = companyController.getCompanyById(companyId);
            if(company != null){
                brandingList.forEach(branding -> {
                    Branding alreadyExistBranding = brandingController.getBrandingByLabelAndCompany(branding.getLabel(), company);
                    if (alreadyExistBranding != null) {
                        alreadyExistBranding.setValue(branding.getValue());
                        company.setBranding(brandingController.saveBranding(alreadyExistBranding));
                    } else {
                        branding.setCompany(company);
                        company.setBranding(brandingController.saveBranding(branding));
                    }
                });
                companyController.saveCompany(company);
                response.setData(company);
                response.setSuccess(true);
                response.setShowToast(false);
                response.setStatus(HttpStatus.OK);
            }else{
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                response.setSuccess(false);
                response.setDoLogout(true);
            }
        }
        return response;
    }

    @GetMapping(path = "/brandingreset")
    public ApiResponse<?> resetBranding(@RequestParam("id") Long companyId, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<SetBrandingResponse> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else {
            Company company = companyController.getCompanyById(companyId);
            company.getBranding().forEach(branding -> {
                brandingController.deleteBranding(branding);
            });
            response.setSuccess(true);
            response.setShowToast(false);
            response.setStatus(HttpStatus.OK);
        }
        return response;
    }

    @PostMapping("/upload-company-logo")
    public ResponseEntity<ApiResponse<Company>> updateCompanyLogo(@RequestParam("id") Long companyId,
                                                                  @RequestParam("file") MultipartFile file,
                                                                  HttpServletRequest request) {
        ApiResponse<Company> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        }else{
            Company company = companyController.getCompanyById(companyId);
            if(company == null){
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            }else {
                try {
                    Map<String, String> uploadResponse = cloudinaryController.uploadCompanyLogo(file);
                    String pictureUrl = uploadResponse.get("url");
                    String pictureId = uploadResponse.get("publicId");
                    if(company.getLogoURL() != null && !company.getLogoURL().isEmpty() ) cloudinaryController.deleteFile(company.getLogoPublicId());
                    company.setLogoURL(pictureUrl);
                    company.setLogoPublicId(pictureId);
                    company = companyController.saveCompany(company);
                    response.setStatus(HttpStatus.OK);
                    response.setMessageLabel("branding_company_logo_update_done");
                    response.setData(company);
                    response.setSuccess(true);

                } catch (Exception e) {
                    response.setData(null);
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    response.setSuccess(false);
                    response.setMessageLabel("branding_company_logo_update_failed");
                }
            }
        }
        return new ResponseEntity<>( response , response.getStatus());
    }
}
