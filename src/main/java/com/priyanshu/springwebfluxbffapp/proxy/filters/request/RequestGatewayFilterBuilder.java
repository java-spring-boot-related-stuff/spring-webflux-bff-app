package com.priyanshu.springwebfluxbffapp.proxy.filters.request;

import com.priyanshu.springwebfluxbffapp.proxy.model.FilterDef;

public interface RequestGatewayFilterBuilder {

    public RequestGatewayFilter apply(FilterDef filterDef);

}
