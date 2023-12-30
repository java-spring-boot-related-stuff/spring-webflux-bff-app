package com.priyanshu.springwebfluxbffapp.proxy.predicate;

import com.priyanshu.springwebfluxbffapp.proxy.model.PredicateDef;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class QueryPredicateBuilder implements PredicateBuilder {

    private final static PathPatternParser pathPatternParser= new PathPatternParser();

    @Override
    public Predicate<ServerWebExchange> apply(PredicateDef predicateDef) {

        int argsSize= predicateDef.getArgs().size();
        String queryParamName= predicateDef.getArgs().get(0);
        List<String> queryValues= new ArrayList<>();
        for(int i=1;i<argsSize;i++) {
            queryValues.add(predicateDef.getArgs().get(i));
        }

        if(argsSize == 1) {
            return serverWebExchange -> serverWebExchange.getRequest().getQueryParams().containsKey(queryParamName);
        }

        return serverWebExchange ->
                    serverWebExchange.getRequest().getQueryParams().containsKey(queryParamName)
                    &&
                    queryValues.contains(serverWebExchange.getRequest().getQueryParams().getFirst(queryParamName));

    }
}
