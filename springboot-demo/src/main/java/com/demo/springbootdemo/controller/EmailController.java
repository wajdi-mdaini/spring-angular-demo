package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.configuration.EmailConfigProperties;
import com.demo.springbootdemo.entity.User;
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
     * Send delete user email
     */
    public void sendDeleteUserEmail(String name, String email, String companyName)
            throws MessagingException {

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("currentYear", new Date().getYear() + 1900);
        context.setVariable("email", email);
        context.setVariable("companyName", companyName.toUpperCase());

        String subject = "WorkSync Account Removal – Confirmation of Deletion";
        String htmlContent = templateEngine.process("email/user-deleted-notification", context);

        sendHtmlEmail(email, subject, htmlContent);
    }
    /**
     * Send welcome email with generated password to new user
     */
    public void sendWelcomePasswordEmail(String name,
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

        sendHtmlEmail(email, subject, htmlContent);
    }

    public void sendResetPasswordConfirmationEmail(String email, String generatedCode,User user,long verificationExpireIn)
            throws MessagingException {
        Context context = new Context();
        context.setVariable("name", user.getFirstname() + " " + user.getLastname());
        context.setVariable("code", generatedCode);
        context.setVariable("currentYear", new Date().getYear() + 1900);
        context.setVariable("companyName", user.getTeam().getCompany().getName().toUpperCase());
        context.setVariable("verificationExpireIn", verificationExpireIn);

        String subject = "WorkSync Platform - Password Reset Confirmation";
        String htmlContent = templateEngine.process("email/password-confirmation", context);

        sendHtmlEmail(email, subject, htmlContent);
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
