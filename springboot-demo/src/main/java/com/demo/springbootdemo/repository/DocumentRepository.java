package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Document;
import com.demo.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByTo(User user);
}
