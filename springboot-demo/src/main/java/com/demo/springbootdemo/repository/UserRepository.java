package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Role;
import com.demo.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    List<User> findByRoleAndTeamIsNull(Role role);
}
