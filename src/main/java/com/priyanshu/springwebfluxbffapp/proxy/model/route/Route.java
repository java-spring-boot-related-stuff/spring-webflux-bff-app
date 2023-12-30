package com.priyanshu.springwebfluxbffapp.proxy.model.route;

import com.priyanshu.springwebfluxbffapp.proxy.model.Host;
import com.priyanshu.springwebfluxbffapp.proxy.model.Upstream;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.function.Predicate;

public class Route {

    private String id;
    private Host host;
    private Upstream upstream;
    private List<Predicate<ServerWebExchange>> predicates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Upstream getUpstream() {
        return upstream;
    }

    public void setUpstream(Upstream upstream) {
        this.upstream = upstream;
    }

    public List<Predicate<ServerWebExchange>> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<Predicate<ServerWebExchange>> predicates) {
        this.predicates = predicates;
    }
}
