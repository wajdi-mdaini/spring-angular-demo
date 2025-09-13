package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter @Getter
public class ApiResponse<T> {
    private HttpStatus status;
    private String messageLabel;
    private T data;
    private boolean success;
    private boolean showToast = true;
    private boolean doLogout = false;
    public ApiResponse() {}
}
