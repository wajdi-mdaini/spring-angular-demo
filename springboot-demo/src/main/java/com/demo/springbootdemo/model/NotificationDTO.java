package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificationDTO {
    private Long id;
    private Long at;
    private String titleLabel;
    private String messageLabel;
    private String fromName;
    private String fromId;
    private String fromProfilePictureUrl;
    private String toEmail;
    private Boolean read;

}
