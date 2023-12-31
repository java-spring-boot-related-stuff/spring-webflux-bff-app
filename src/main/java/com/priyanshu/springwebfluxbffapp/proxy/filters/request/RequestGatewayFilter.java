package com.priyanshu.springwebfluxbffapp.proxy.filters.request;

import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayRequest;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface RequestGatewayFilter {

    Mono<Void> process(GatewayRequest request);

}
