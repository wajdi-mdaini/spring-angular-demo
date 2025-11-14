package com.demo.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CompanySettings {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int verificationCodeLength = 5; // minutes
    private int verificationCodeExpireIn = 2; // minutes

    private int jwtTokenExpireIn = 30; // minutes

    private Float holidayDaysPerMonth = 2.4f; // day
    private int sicknessLeaverDaysPerYear = 10; // day
    private int passwordMinLength = 8; // day
}
