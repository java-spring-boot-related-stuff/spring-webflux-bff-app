package com.priyanshu.springwebfluxbffapp.proxy.model.route;

import com.priyanshu.springwebfluxbffapp.proxy.model.Upstream;

import java.util.List;

public class RawRouteProperties{

    private String id;
    private String hostId;
    private Upstream upstream;

    private List<String> predicates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Upstream getUpstream() {
        return upstream;
    }

    public void setUpstream(Upstream upstream) {
        this.upstream = upstream;
    }
    

    public List<String> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<String> predicates) {
        this.predicates = predicates;
    }
}
