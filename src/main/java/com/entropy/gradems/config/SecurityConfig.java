package com.entropy.gradems.config;

import com.entropy.gradems.jwt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // 定义JwtUtil Bean
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    // 定义JwtAuthenticationManager Bean
    @Bean
    public JwtAuthenticationManager jwtAuthenticationManager() {
        return new JwtAuthenticationManager(jwtUtil());
    }

    // 定义JwtSecurityContextRepository Bean，依赖JwtAuthenticationManager
    @Bean
    public JwtSecurityContextRepository jwtSecurityContextRepository() {
        return new JwtSecurityContextRepository(jwtAuthenticationManager());
    }

    // 定义JwtAuthenticationFilter Bean，依赖JwtSecurityContextRepository
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtSecurityContextRepository());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(jwtAuthenticationManager())
                .securityContextRepository(jwtSecurityContextRepository())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/register").permitAll()
                        .pathMatchers("/getAllDeparts").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }
}