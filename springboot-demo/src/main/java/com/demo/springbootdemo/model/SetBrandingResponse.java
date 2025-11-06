package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.Branding;
import com.demo.springbootdemo.entity.Company;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class SetBrandingResponse {
    private List<Branding> brandingList;
    private Company company;
}
