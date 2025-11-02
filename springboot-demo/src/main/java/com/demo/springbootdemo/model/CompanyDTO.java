package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompanyDTO {
    private Long companyId;
    private String companyName;
    private String companyEmail;
    private String description;
    private Long companyPhone;
    private String companyWebLink;
    private String companyAddress;
}
