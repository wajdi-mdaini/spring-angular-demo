package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByCompany(Company company);
}
