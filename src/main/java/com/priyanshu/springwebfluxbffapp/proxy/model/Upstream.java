package com.priyanshu.springwebfluxbffapp.proxy.model;

import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class Upstream {

    private String contextPath;

    private HttpMethod method;
}
