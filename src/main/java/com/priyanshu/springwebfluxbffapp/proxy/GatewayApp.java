package com.priyanshu.springwebfluxbffapp.proxy;

import com.priyanshu.springwebfluxbffapp.proxy.condition.GatewayAppEnabledCondition;
import com.priyanshu.springwebfluxbffapp.proxy.config.GatewayAppConfig;
import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayRequest;
import com.priyanshu.springwebfluxbffapp.proxy.model.route.Route;
import com.priyanshu.springwebfluxbffapp.proxy.utils.ServerWebExchangeUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Conditional(GatewayAppEnabledCondition.class)
public class GatewayApp {

    private final GatewayAppConfig gatewayAppConfig;

    public GatewayApp(GatewayAppConfig gatewayAppConfig) {
        this.gatewayAppConfig= gatewayAppConfig;
    }

    public Mono<ResponseEntity<Object>> handle(ServerWebExchange exchange, DataBuffer requestBody) {
        Route selectedRoute=gatewayAppConfig.selectRoute(exchange);
        GatewayRequest gatewayRequest= ServerWebExchangeUtils.createGatewayRequest(exchange, selectedRoute, requestBody);

        ProxyExchange proxyExchange= new ProxyExchange<>()
                .gatewayRequest(gatewayRequest)
                .build();

        return proxyExchange.exchange();

    }
}
