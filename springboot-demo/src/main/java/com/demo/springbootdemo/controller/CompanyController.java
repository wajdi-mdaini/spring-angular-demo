package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    public Company isCompanyExist(Company company){
        boolean isExist = companyRepository.findByNameIgnoreCase(company.getName().trim()).isEmpty();
        if(!isExist){
            return null;
        }
        return company;
    }

    public Company setCompany(Company company){
        Optional<Company> companyRepositoryById = companyRepository.findById(company.getId());
        return companyRepository.save(company);
    }

    public Company saveCompany(Company company){
        return  companyRepository.save(company);
    }
}
