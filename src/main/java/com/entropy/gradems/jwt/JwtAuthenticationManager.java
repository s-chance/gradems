package com.entropy.gradems.jwt;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        try {
            String username = jwtUtil.getUsernameFromToken(authToken);
            if (!jwtUtil.isTokenExpired(authToken)) {
                List<String> roles = jwtUtil.getRolesFromToken(authToken);
                List<SimpleGrantedAuthority> authorities =
                        roles
                                .stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                .collect(Collectors.toList());
                Authentication auth = new UsernamePasswordAuthenticationToken(username, authToken, authorities);
                return Mono.just(auth);
            }
            return Mono.empty();
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
