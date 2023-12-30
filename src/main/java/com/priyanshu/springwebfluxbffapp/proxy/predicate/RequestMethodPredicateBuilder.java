package com.priyanshu.springwebfluxbffapp.proxy.predicate;

import com.priyanshu.springwebfluxbffapp.proxy.model.PredicateDef;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.function.Predicate;

public class RequestMethodPredicateBuilder implements PredicateBuilder {

    @Override
    public Predicate<ServerWebExchange> apply(PredicateDef predicateDef) {

        List<HttpMethod> allowedMethodsStrList= predicateDef
                .getArgs().stream().map(HttpMethod::valueOf)
                .toList();

        return serverWebExchange -> allowedMethodsStrList.contains(serverWebExchange.getRequest().getMethod());

    }
}
