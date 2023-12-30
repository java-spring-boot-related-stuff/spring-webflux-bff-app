package com.priyanshu.springwebfluxbffapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http.authorizeExchange(authorizeExchangeSpec -> {
            authorizeExchangeSpec.anyExchange().permitAll();
        });

        http.csrf(csrfSpec -> {
            csrfSpec.disable();
        });

        return http.build();
    }

}
