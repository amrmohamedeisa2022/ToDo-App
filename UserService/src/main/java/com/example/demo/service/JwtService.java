package com.example.demo.service;

import com.example.demo.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;

public interface JwtService {

    String extractEmail(String token);

    Claims parseJwtClaims(String token);

    String createToken(User user, Map<String, Object> extraClaims);

    boolean isTokenValid(String accessToken, UserDetails userDetails);

    String resolveToken(HttpServletRequest request);

    Claims resolveClaims(HttpServletRequest request);

    Boolean isTokenExpired(Date expiration);

    UserDetails getCurrentUserDetails();
}
