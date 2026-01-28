package com.example.demo.service;

import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
   private UserRepository userRepository;

   @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public User createUser(User user) {
        if(userRepository.existsByEmail(user.getEmail()))
        {
           throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    public User updateUser(String authorization, User user) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setEnabled(existingUser.isEnabled());

        return userRepository.save(existingUser);
    }


    @Override
    public void deleteUserByEmail(String email) {
     User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

       userRepository.delete(user);

    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    @Override
    public void forgetPassword(String authorization) {


        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }


        String token = authorization.replace("Bearer ", "").trim();


        String email = jwtService.extractEmail(token);


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Otp otp = otpService.generateOtp(user);
        otpRepository.save(otp);

        emailService.sendOtpMsg(
                user.getEmail(),
                "Reset Password Code",
                otp.getOtp()
        );
    }

    @Override
    public User searchUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public void deleteUser(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }



    @Override
    public void changePassword(String token, String otp, String newPassword) {


        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String jwt = token.replace("Bearer ", "").trim();

        String email = jwtService.extractEmail(jwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Otp savedOtp = otpRepository.findTopByUserOrderByExpirationTimeDesc(user)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!savedOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (savedOtp.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRepository.delete(savedOtp);
    }


    @Override
    public void activateUser(String email, String otp) {
   User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

   Otp savedOtp=otpRepository.findTopByUserOrderByExpirationTimeDesc(user)
           .orElseThrow(() -> new RuntimeException("OTP not found"));

   if(!savedOtp.getOtp().equals(otp))
       throw new RuntimeException("Invalid OTP");

   if(savedOtp.getExpirationTime().isBefore(LocalDateTime.now()))
       throw new RuntimeException("OTP expired");

   user.setEnabled(true);
   userRepository.save(user);

   otpRepository.delete(savedOtp);

    }

    @Override
    public void regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Otp newOtp = otpService.generateOtp(user);
        otpRepository.save(newOtp);

        emailService.sendOtpMsg(
                user.getEmail(),
                "Your New OTP Code",
                newOtp.getOtp()
        );
    }


    @Override
    public String validateToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();

        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean valid = jwtService.isTokenValid(token, user);

        if (valid) {
            return "Token is valid";
        } else {
            throw new RuntimeException("Token is invalid");
        }
    }

}
