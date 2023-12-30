package com.priyanshu.springwebfluxbffapp.controller;

import com.priyanshu.springwebfluxbffapp.proxy.GatewayApp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class WebFluxApplicationController {

    private final GatewayApp gatewayApp;

    public WebFluxApplicationController(GatewayApp gatewayApp) {
        this.gatewayApp= gatewayApp;
    }

    @GetMapping("/**")
    public Mono<ResponseEntity<Object>> handleGet(ServerWebExchange exchange, @RequestBody(required = false) DataBuffer requestBody) {
        return this.gatewayApp.handle(exchange,requestBody);
    }

    @PostMapping("/**")
    public Mono<ResponseEntity<Object>> handlePost(ServerWebExchange exchange, @RequestBody(required = false) DataBuffer requestBody) {
        return this.gatewayApp.handle(exchange,requestBody);
    }


}