package com.priyanshu.springwebfluxbffapp.proxy.model;

import org.springframework.http.HttpHeaders;

public class GatewayResponse<T> {

    private int statusCode;
    private HttpHeaders headers;
    private T body;

    private String errorBody;

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}