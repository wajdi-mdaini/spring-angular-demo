package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Document;
import com.demo.springbootdemo.entity.Notification;
import com.demo.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findById(Long id);

    List<Document> findByToOrderByAtDesc(User user);

    List<Document> findByFromOrderByAtDesc(User from);
}
