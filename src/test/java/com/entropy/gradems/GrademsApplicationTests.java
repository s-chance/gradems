package com.entropy.gradems;


import com.entropy.gradems.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class GrademsApplicationTests {


    @Test
    void contextLoads() throws ParseException {


//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        studentMapper.deleteGraduate(format.parse("2016-09-01"), 120);
    }

    private JwtUtil jwtUtil = new JwtUtil();

    @Test
    void testGenerateAndValidateToken() {
        String username = "testUser";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        // Generate token
        String token = jwtUtil.generateToken(username, roles);
        assertNotNull(token);

        // Validate token and extract claims
        Claims claims = jwtUtil.validateToken(token);
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertTrue(claims.get("roles") instanceof List);
        assertEquals(roles, claims.get("roles", List.class));

        // Check if token is not expired
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        // Assuming we have a very short expiry time for testing
//        jwtUtil.setExpiration(10); // Set token expiration to 10 milliseconds for the test

        String username = "testUser";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        // Generate token
        String token = jwtUtil.generateToken(username, roles);
        Thread.sleep(20); // Wait longer than the token expiration time

        // Verify the token is expired
        assertTrue(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testToken() {
        String username = "user123";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        // Generate token with user information
        String token = jwtUtil.generateToken(username, roles);
        System.out.println("Generated Token: " + token);

        // Assume token is being sent to and received from a client in a real application

        // Validate token and extract user information
        Claims claims = jwtUtil.validateToken(token);
        String extractedUsername = claims.getSubject();
        List<String> extractedRoles = claims.get("roles", List.class);

        System.out.println("Username from Token: " + extractedUsername);
        System.out.println("Roles from Token: " + extractedRoles);
    }

}
