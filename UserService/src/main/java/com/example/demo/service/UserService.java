package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.model.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {

    User createUser(User user);
    User updateUser(String authorization, User user);
    void deleteUserByEmail(String email);
    Optional<User> getUserByEmail(String email);
     void deleteUser(String authorization);
    public User searchUser(String email);
    void createProfile(String authorization, String name, MultipartFile photo);
    byte[] getProfileImage(String authorization);
    ProfileResponse getProfile(String authorization);
    void forgetPassword(String token);
    void changePassword(String token, String otp, String newPassword);
    void activateUser(String email, String otp);
    void resetPassword(String email, String otp, String newPassword);
    String validateToken (String Authorization);

}
