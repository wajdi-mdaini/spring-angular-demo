package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Document;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    public List<Document> findByTo(User user) {
        return documentRepository.findByTo(user);
    }

    public void deleteDocument(Document document) {
        documentRepository.delete(document);
    }
}
