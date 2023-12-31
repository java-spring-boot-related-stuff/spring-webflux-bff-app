package com.priyanshu.springwebfluxbffapp.proxy.filters.response;

import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayResponse;
import reactor.core.publisher.Mono;

public interface ResponseGatewayFilter {
    Mono<Void> process(GatewayResponse<Object> response);


}
