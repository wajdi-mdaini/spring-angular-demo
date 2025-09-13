package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    public Company createCompany(Company company){
        boolean isExist = companyRepository.findByNameIgnoreCase(company.getName().trim()).isEmpty();
        if(!isExist){
            return null;
        }
        return companyRepository.save(company);
    }
}
