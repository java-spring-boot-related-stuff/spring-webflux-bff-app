package com.priyanshu.springwebfluxbffapp.proxy.model;

import com.priyanshu.springwebfluxbffapp.proxy.model.route.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GatewayRequest {

    private Host host;

    private Route route;

    private String absoluteUri;

    private HttpMethod httpMethod;

    private HttpHeaders headers= new HttpHeaders();

    private MultiValueMap<String, String> queries= new LinkedMultiValueMap<>();

    private Object body;

    private Map<String, String> pathParams= new HashMap<>();

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route routeProperties) {
        this.route = routeProperties;
    }

    public String getAbsoluteUri() {
        return absoluteUri;
    }

    public void setAbsoluteUri(String absoluteUri) {
        this.absoluteUri = absoluteUri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeader(String name, String value) {
        name= name.toLowerCase();
        this.headers.put(name, new ArrayList<>(List.of(value)));
    }

    public void appendHeader(String name, String value) {
        name= name.toLowerCase();
        List<String> headerValues= this.headers.getOrDefault(name, new ArrayList<>());
        headerValues.add(value);
        this.headers.put(name, headerValues);
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers.putAll(headers);
    }

    public MultiValueMap<String, String> getQueries() {
        return queries;
    }

    public void setQuery(String name, String value) {
        this.queries.put(name, new ArrayList<>(List.of(value)));
    }

    public void appendQuery(String name, String value) {
        List<String> queryValues= this.headers.getOrDefault(name, new ArrayList<>());
        queryValues.add(value);
        this.queries.put(name, queryValues);
    }

    public void setQueries(MultiValueMap<String, String> queries) {
        this.queries.putAll(queries);
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body= body;
    }

    public String getPathParam(String key) {
        return this.pathParams.get(key);
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }
}
