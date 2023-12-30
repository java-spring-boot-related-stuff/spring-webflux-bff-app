package com.priyanshu.springwebfluxbffapp.proxy.config;

import com.priyanshu.springwebfluxbffapp.proxy.condition.GatewayAppEnabledCondition;
import com.priyanshu.springwebfluxbffapp.proxy.predicate.PathPredicateBuilder;
import com.priyanshu.springwebfluxbffapp.proxy.predicate.PredicateBuilder;
import com.priyanshu.springwebfluxbffapp.proxy.predicate.QueryPredicateBuilder;
import com.priyanshu.springwebfluxbffapp.proxy.predicate.RequestMethodPredicateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(GatewayAppEnabledCondition.class)
public class PredicatesConfig {

    @Bean("PathPredicate")
    public PredicateBuilder pathPredicate() {
        return new PathPredicateBuilder();
    }

    @Bean("QueryPredicate")
    public PredicateBuilder queryPredicate() {
        return new QueryPredicateBuilder();
    }


    @Bean("RequestMethodPredicate")
    public PredicateBuilder requestMethodPredicate() {
        return new RequestMethodPredicateBuilder();
    }

}
