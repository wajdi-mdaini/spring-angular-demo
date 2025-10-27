package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findById(Long idCompany);
    List<Company> findByNameIgnoreCase(String companyName);
    Company findByMembersContains(User user);
}
