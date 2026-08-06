package com.example.demo.service;

import com.example.demo.entity.JwtToken;
import com.example.demo.entity.Otp;
import com.example.demo.entity.TokenType;
import com.example.demo.entity.User;
import com.example.demo.exceptions.CustomException;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.RegisterRequest;
import com.example.demo.model.response.AuthenticationResponse;
import com.example.demo.repository.JwtTokenRepository;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailServiceImpl emailService;


    public AuthenticationResponse login(LoginRequest loginRequest) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Wrong password");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (!user.isEnabled()) {
            throw new CustomException("Account is not activated");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        String token = jwtService.createToken(user, extraClaims);

        saveUserToken(user, token);

        return new AuthenticationResponse(token, user.getEmail());
    }



    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .build();

        User savedUser = userRepository.save(user);

        Otp otp=otpService.generateOtp(savedUser);
        otpRepository.save(otp);


        emailService.sendOtpMsg(
                savedUser.getEmail(),
                "Account Activation Code",
                otp.getOtp()
        );

        return "Registration successful. Please check your email for OTP.";
    }

    private void saveUserToken(User user, String jwtToken) {

        JwtToken token = JwtToken.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .createdAt(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusHours(1))
                .build();

        jwtTokenRepository.save(token);
    }

}
