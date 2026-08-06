package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class TodoServiceApplicationTests {

    @Autowired
    private RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/todo/item";

    @Test
    void testReactivateItem() {
        String url = baseUrl + "/id/1/activated";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

