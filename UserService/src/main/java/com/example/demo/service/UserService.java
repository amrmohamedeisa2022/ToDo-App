package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.Optional;

public interface UserService {

    User createUser(User user);
    User updateUser(String authorization, User user);
    void deleteUserByEmail(String email);
    Optional<User> getUserByEmail(String email);
     void deleteUser(String authorization);
    public User searchUser(String email);
    void forgetPassword(String token);
    void changePassword(String token, String otp, String newPassword);
    void activateUser(String email, String otp);
    void regenerateOtp(String email);
    String validateToken (String Authorization);

}
