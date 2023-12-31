package com.priyanshu.springwebfluxbffapp.proxy.filters.response;

import com.priyanshu.springwebfluxbffapp.proxy.model.FilterDef;

public interface ResponseGatewayFilterBuilder {

    public ResponseGatewayFilter apply(FilterDef filterDef);

}
