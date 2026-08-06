package com.example.demo.service;

import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    public Otp generateOtp(User user) {

        String otpCode = generateRandomOtp();

        return Otp.builder()
                .otp(otpCode)
                .expirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES))
                .user(user)
                .build();
    }

    private String generateRandomOtp() {
        int otp = (int) (Math.random() * Math.pow(10, OTP_LENGTH));
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }
}

