package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
