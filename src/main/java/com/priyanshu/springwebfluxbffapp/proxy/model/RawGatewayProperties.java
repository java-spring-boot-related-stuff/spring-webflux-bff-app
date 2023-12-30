package com.priyanshu.springwebfluxbffapp.proxy.model;

import com.priyanshu.springwebfluxbffapp.proxy.model.route.RawRouteProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "gateway-app")
public class RawGatewayProperties {

    private Boolean enabled;

    private List<Host> hosts;

    private List<RawRouteProperties> routes;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public List<RawRouteProperties> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RawRouteProperties> routes) {
        this.routes = routes;
    }
}
