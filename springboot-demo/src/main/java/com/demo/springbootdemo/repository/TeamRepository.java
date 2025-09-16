package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
