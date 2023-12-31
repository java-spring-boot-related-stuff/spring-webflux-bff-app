package com.priyanshu.springwebfluxbffapp.proxy.model.route;

import com.priyanshu.springwebfluxbffapp.proxy.model.Upstream;

import java.util.List;

public class RawRouteProperties{

    private String id;
    private String hostId;

    private List<String> predicates;
    private Upstream upstream;

    private List<String> requestModifiers;

    private List<String> responseModifiers;

    public List<String> getRequestModifiers() {
        return requestModifiers;
    }

    public void setRequestModifiers(List<String> requestModifiers) {
        this.requestModifiers = requestModifiers;
    }

    public List<String> getResponseModifiers() {
        return responseModifiers;
    }

    public void setResponseModifiers(List<String> responseModifiers) {
        this.responseModifiers = responseModifiers;
    }

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
