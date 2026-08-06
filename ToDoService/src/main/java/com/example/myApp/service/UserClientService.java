package com.example.myApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserClientService {

    @Autowired
    private RestTemplate restTemplate;

    private final String USER_SERVICE_VALIDATE_TOKEN_URL =
            "http://localhost:8080/api/home/validateToken";


    public void validateToken(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    USER_SERVICE_VALIDATE_TOKEN_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Token is invalid");
            }

        } catch (Exception e) {
            throw new RuntimeException("Token validation failed");
        }
    }
}
