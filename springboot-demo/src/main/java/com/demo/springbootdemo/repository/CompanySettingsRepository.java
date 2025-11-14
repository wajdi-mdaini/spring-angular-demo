package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {
    CompanySettings findById(long id);
}
