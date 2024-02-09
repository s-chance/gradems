package com.entropy.gradems.jwt;


import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


public class JwtAuthenticationFilter implements WebFilter {

    private final JwtSecurityContextRepository jwtSecurityContextRepository;

    public JwtAuthenticationFilter(JwtSecurityContextRepository jwtSecurityContextRepository) {
        this.jwtSecurityContextRepository = jwtSecurityContextRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return jwtSecurityContextRepository.load(exchange)
                .flatMap(context -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context))))
                .switchIfEmpty(chain.filter(exchange));
    }
}