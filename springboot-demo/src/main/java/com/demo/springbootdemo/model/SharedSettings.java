package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter @Getter
@ConfigurationProperties(prefix = "app.shared")
public class SharedSettings {
    private int verificationCodeLength;

    public int getVerificationCodeLength() {
        return Math.min(verificationCodeLength, 6);
    }
}
