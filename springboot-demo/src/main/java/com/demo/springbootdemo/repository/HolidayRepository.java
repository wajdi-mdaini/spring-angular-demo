package com.demo.springbootdemo.repository;

import com.demo.springbootdemo.entity.Holiday;
import com.demo.springbootdemo.entity.HolidayStatus;
import com.demo.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday,Long> {
    Holiday findById(long id);

    List<Holiday> findByUserOrderByAtDesc(User user);

    List<Holiday> findByStatus(HolidayStatus status);

    List<Holiday> findByStatusAndUser(HolidayStatus status, User user);
}
