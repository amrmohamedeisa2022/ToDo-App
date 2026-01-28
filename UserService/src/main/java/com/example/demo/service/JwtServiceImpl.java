package com.example.demo.service;


import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

     @Value("${jwt.secret}")
    private String secretKey;

     @Value("${jwt.access.expiration-ms}")
     private Long accessTokenValidity;

     private JwtParser jwtParser;

     @PostConstruct
     public void init() {
         this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
     }

    @Override
    public String createToken(User user, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenValidity))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }


    @Override
    public String extractEmail(String token) {
        return parseJwtClaims(token).getSubject();
    }


    @Override
    public boolean isTokenValid(String accessToken, UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            Claims claims = parseJwtClaims(accessToken);

            return username.equals(claims.getSubject())
                    && !isTokenExpired(claims.getExpiration());

        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public String resolveToken(HttpServletRequest request) {
     String bearerToken = request.getHeader("Authorization");
     if(bearerToken!=null&&bearerToken.startsWith("Bearer ")){
         return bearerToken.substring(7);
     }
     return null;
    }

    @Override
    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public Boolean isTokenExpired(Date expirationDate) {
        try {
            if(expirationDate.before(new Date()))
                return true;
            else
                return false;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public UserDetails getCurrentUserDetails() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }

        return null;
    }

}
