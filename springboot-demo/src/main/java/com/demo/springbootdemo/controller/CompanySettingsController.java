package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.CompanySettings;
import com.demo.springbootdemo.repository.CompanySettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CompanySettingsController {

    @Autowired
    private CompanySettingsRepository companySettingsRepository;

    public CompanySettings saveSettings(CompanySettings companySettings){
        return companySettingsRepository.save(companySettings);
    }

    public CompanySettings findById(Long idCompanySettings){
        return companySettingsRepository.findById(idCompanySettings).orElse(null);
    }
}
