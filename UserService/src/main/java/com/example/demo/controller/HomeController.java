package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/regenerateOtp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String email) {
        userService.regenerateOtp(email);
        return ResponseEntity.ok("OTP sent successfully");
    }


    @Operation(summary = "Forget password send OTP ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,Look at your email ")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No users found with this email in token")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")

    })
    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestHeader("Authorization") String authorization) {
        userService.forgetPassword(authorization);
        return ResponseEntity.ok("OTP sent to your email");
    }



    @Operation(summary = "Change password via OTP send ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,Password changed ")
            ,@ApiResponse(responseCode = "400",description = "BAD_REQUEST, CHECK YOUR OTP")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No users found with this email in token")
    })
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String otp,
            @RequestParam String newPassword
    ) {
        userService.changePassword(authorization, otp, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/validateToken")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userService.validateToken(authorization));
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(
            @RequestHeader("Authorization") String authorization,
            @RequestBody User user
    ) {
        return ResponseEntity.ok(userService.updateUser(authorization, user));
    }



    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String authorization) {
        userService.deleteUser(authorization);
        return ResponseEntity.ok("User deleted successfully");
    }

}
