package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.model.request.UpdateUserRequest;
import com.example.demo.model.response.ProfileResponse;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/home")
@Tag(name = "Home Controller")
public class HomeController {

    @Autowired
    private UserService userService;

    @PostMapping("/activateUser")
    public ResponseEntity<String> activateUser(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        userService.activateUser(email, otp);
        return ResponseEntity.ok("Account activated successfully");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword
    ) {
        userService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }


    @PostMapping("/createProfile")
    public ResponseEntity<String> createProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile photo
    ) {
        userService.createProfile(authorization, name, photo);
        return ResponseEntity.ok("Profile created successfully");
    }


    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            @RequestHeader("Authorization") String authorization
    ) {
        return ResponseEntity.ok(userService.getProfile(authorization));
    }


    @GetMapping("/profile/image")
    public ResponseEntity<byte[]> getProfileImage(
            @RequestHeader("Authorization") String authorization
    ) {
        byte[] image = userService.getProfileImage(authorization);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }


    @Operation(summary = "Forget password send OTP ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,Look at your email ")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No users found with this email in token")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")

    })
    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestParam String email) {
        userService.forgetPassword(email);
        return ResponseEntity.ok("OTP sent to your email");
    }



    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        userService.changePassword(authorization, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }



    @GetMapping("/validateToken")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userService.validateToken(authorization));
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(authorization, request));
    }




    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String authorization) {
        userService.deleteUser(authorization);
        return ResponseEntity.ok("User deleted successfully");
    }

}
