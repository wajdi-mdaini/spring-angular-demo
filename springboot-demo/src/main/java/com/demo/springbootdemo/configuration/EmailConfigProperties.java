package com.demo.springbootdemo.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.mail")
@Getter @Setter
public class EmailConfigProperties {
    private String username;
    private String password;
}
