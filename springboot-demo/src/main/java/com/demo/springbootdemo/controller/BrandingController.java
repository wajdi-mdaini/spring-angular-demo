package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Branding;
import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.repository.BrandingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BrandingController {

    @Autowired
    private BrandingRepository brandingRepository;

    public Branding saveBranding(Branding branding) {
        return brandingRepository.save(branding);
    }

    public Branding getBrandingByLabelAndCompany(String label, Company company) {
        List<Branding> brandingList = brandingRepository.findByLabelAndCompany(label,company);
        if(!brandingList.isEmpty()){
            return brandingList.get(0);
        }
        return null;
    }

    public void deleteBranding(Branding branding) {
        brandingRepository.delete(branding);
    }
}
