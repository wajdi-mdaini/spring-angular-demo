package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByAtDesc();
    Optional<Event> findById(Long id);
}
