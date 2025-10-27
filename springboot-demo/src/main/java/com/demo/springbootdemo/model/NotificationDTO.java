package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificationDTO {
    Long id;
    Long at;
    String titleLabel;
    String messageLabel;
    String fromName;
    String fromId;
    String fromProfilePictureUrl;
    String toEmail;
    Boolean read;

}
