package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    public Company addCompany(Company company){
        return companyRepository.save(company);
    }
}
