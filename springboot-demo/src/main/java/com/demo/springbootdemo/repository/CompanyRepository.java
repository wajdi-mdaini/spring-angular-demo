package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findCompanyByNameIgnoreCase(String companyName);
    List<Company> findByNameIgnoreCase(String companyName);
}
