package com.demo.springbootdemo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentDTO {
    String name;
    String description;
    String toUserEmail;
    Long documentId;
}
