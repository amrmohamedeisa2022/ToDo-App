package com.example.demo.service;

import com.example.demo.exceptions.CustomException;
import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import com.example.demo.model.request.UpdateUserRequest;
import com.example.demo.model.response.ProfileResponse;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
           throw new CustomException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    public User updateUser(String authorization, UpdateUserRequest request) {

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        if (request.getName() != null)
            existingUser.setName(request.getName());

        if (request.getPassword() != null && !request.getPassword().isBlank())
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));


        return userRepository.save(existingUser);
    }



    @Override
    public void deleteUserByEmail(String email) {
     User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

       userRepository.delete(user);

    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    @Override
    public void forgetPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found with this email"));

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
                .orElseThrow(() -> new CustomException("User not found with email: " + email));
    }

    @Override
    public void createProfile(String authorization, String name, MultipartFile photo) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setName(name);

        try {
            if (photo != null && !photo.isEmpty()) {
                user.setProfileImage(photo.getBytes());
            }
        } catch (Exception e) {
            throw new CustomException("Failed to store image in database");
        }

        userRepository.save(user);
    }

    @Override
    public ProfileResponse getProfile(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        return new ProfileResponse(
                user.getEmail(),
                user.getName(),
                user.getProfileImage() != null
        );
    }

    @Override
    public byte[] getProfileImage(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        if (user.getProfileImage() == null) {
            throw new CustomException("No profile image found");
        }

        return user.getProfileImage();
    }



    @Override
    public void deleteUser(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        userRepository.delete(user);
    }



    @Override
    public void changePassword(String authorization, String oldPassword, String newPassword) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }



    @Override
    public void activateUser(String email, String otp) {
   User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

   Otp savedOtp=otpRepository.findTopByUserOrderByExpirationTimeDesc(user)
           .orElseThrow(() -> new CustomException("OTP not found"));

   if(!savedOtp.getOtp().equals(otp))
       throw new CustomException("Invalid OTP");

   if(savedOtp.getExpirationTime().isBefore(LocalDateTime.now()))
       throw new CustomException("OTP expired");

   user.setEnabled(true);
   userRepository.save(user);

   otpRepository.delete(savedOtp);

    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found with this email"));

        Otp savedOtp = otpRepository.findTopByUserOrderByExpirationTimeDesc(user)
                .orElseThrow(() -> new CustomException("OTP not found"));

        if (!savedOtp.getOtp().equals(otp)) {
            throw new CustomException("Invalid OTP");
        }

        if (savedOtp.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new CustomException("OTP expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRepository.delete(savedOtp);
    }



    @Override
    public String validateToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException("Invalid Authorization header");
        }

        String token = authorization.replace("Bearer ", "").trim();

        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        boolean valid = jwtService.isTokenValid(token, user);

        if (valid) {
            return "Token is valid";
        } else {
            throw new RuntimeException("Token is invalid");
        }
    }

}
