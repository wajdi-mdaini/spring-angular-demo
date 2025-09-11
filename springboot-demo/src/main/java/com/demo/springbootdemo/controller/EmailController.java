package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.configuration.EmailConfigProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

@Service
public class EmailController {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailConfigProperties emailConfig;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${app.front.base.path}")
    private String loginUrl;
    /**
     * Send welcome email with generated password to new user
     */
    public void sendWelcomePasswordEmail(String to, String name,
                                         String email, String generatedPassword, String companyName)
            throws MessagingException {

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("currentYear", new Date().getYear() + 1900);
        context.setVariable("email", email);
        context.setVariable("password", generatedPassword);
        context.setVariable("companyName", companyName.toUpperCase());
        context.setVariable("loginUrl", loginUrl);

        String subject = "Welcome to WorkSync Platform - Your Login Credentials";
        String htmlContent = templateEngine.process("email/welcome-password", context);

        sendHtmlEmail(to, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailConfig.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
