package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetPasswordResponse {

    private String fullName;
    private int verificationCodeLength;
    private Long verificationCodeExpireIn;
    private int passwordMinLength;
    private boolean firstLogin;
}
