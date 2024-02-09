package com.entropy.gradems.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class JwtUtil {
    // 生产环境中应从配置文件或环境变量中安全地获取密钥
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 使用HS256算法生成密钥
    private long expiration = 3600000; // Token有效期，这里示例为1小时（单位毫秒）

    public String generateToken(String username, List<String> roles) {
        // Date now = new Date();
        // Date expiryDate = new Date(now.getTime() + expiration); // 设置Token过期时间
        // 如果不希望使用Date，可以使用以下方法实现
        Instant now = Instant.now();
        Instant expiryDate = now.plus(1, ChronoUnit.HOURS); // 设置Token过期时间为1小时后
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .claim("roles", roles) // 自定义claims，存储用户角色
                .signWith(key) // 使用生成的密钥签名
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(key) // 使用密钥验证
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("roles", List.class);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration().toInstant().isBefore(Instant.now());
    }
}