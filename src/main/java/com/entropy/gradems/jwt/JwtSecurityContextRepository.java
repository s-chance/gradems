package com.entropy.gradems.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class JwtSecurityContextRepository implements ServerSecurityContextRepository {
    private final JwtAuthenticationManager jwtAuthenticationManager;

    public JwtSecurityContextRepository(JwtAuthenticationManager jwtAuthenticationManager) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if ("/login".equals(path) || "/register".equals(path)) {
            return Mono.empty();
        }
        String token = extractToken(exchange.getRequest());
        if (token == null) {
            return Mono.empty();
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
        return this.jwtAuthenticationManager.authenticate(auth).map(SecurityContextImpl::new);
    }

    private String extractToken(HttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
