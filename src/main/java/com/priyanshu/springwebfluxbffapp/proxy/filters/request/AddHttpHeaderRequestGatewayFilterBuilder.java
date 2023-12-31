package com.priyanshu.springwebfluxbffapp.proxy.filters.request;

import com.priyanshu.springwebfluxbffapp.proxy.model.FilterDef;
import jakarta.validation.ValidationException;
import reactor.core.publisher.Mono;

public class AddHttpHeaderRequestGatewayFilterBuilder implements RequestGatewayFilterBuilder{
    @Override
    public RequestGatewayFilter apply(FilterDef filterDef) {

        int argsSize= filterDef.getArgs().size();

        if(argsSize < 2) {
            throw new ValidationException("Arguments Size should be atleast 2 for AddHttpHeaderRequestGatewayFilter but found "+ argsSize);
        }

        String headerName= filterDef.getArgs().get(0);
        String headerValue= filterDef.getArgs().get(1);

        return gatewayRequest -> {
            gatewayRequest.setHeader(headerName, headerValue);
            return Mono.empty();
        };
    }
}