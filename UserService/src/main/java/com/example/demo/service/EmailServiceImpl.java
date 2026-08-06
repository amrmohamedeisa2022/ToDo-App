package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender emailSender;

    public void sendOtpMsg(String email, String subject, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(
                "Your verification code is: " + otp +
                        "\n\nThis code will expire in 5 minutes."
        );

        try {
            emailSender.send(message);
            System.out.println("Mail sent");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
