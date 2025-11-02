package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.CompanyController;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.model.ApiResponse;
import com.demo.springbootdemo.model.CompanyDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/company")
public class CompanyService {

    private final JwtUtil jwtUtil;
    public CompanyService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Autowired
    private CompanyController companyController;

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

}
