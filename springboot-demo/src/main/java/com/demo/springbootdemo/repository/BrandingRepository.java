package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Branding;
import com.demo.springbootdemo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandingRepository extends JpaRepository<Branding,Long> {
    List<Branding> findByLabelAndCompany(String label, Company company);
    List<Branding> findByCompany(Company company);
}
