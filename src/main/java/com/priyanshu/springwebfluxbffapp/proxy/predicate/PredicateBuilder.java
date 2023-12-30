package com.priyanshu.springwebfluxbffapp.proxy.predicate;

import com.priyanshu.springwebfluxbffapp.proxy.model.PredicateDef;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

public interface PredicateBuilder {
    public Predicate<ServerWebExchange> apply(PredicateDef predicateDef);
}
