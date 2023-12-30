package com.priyanshu.springwebfluxbffapp.proxy.utils;

import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayRequest;
import com.priyanshu.springwebfluxbffapp.proxy.model.Host;
import com.priyanshu.springwebfluxbffapp.proxy.model.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class ServerWebExchangeUtils {

    private ServerWebExchangeUtils() {}

    public static final Logger log= LoggerFactory.getLogger(ServerWebExchangeUtils.class);

    public static final String HOST_HEADER_NAME= "host";


    /**
     * Creates an instance of GatewayRequest, which will be used to proxy the request further
     * @param exchange Original Request In form of ServerWebExchange
     * @param route Details of route
     * @param requestBody RequestBody in form of DataBuffer
     * @return An instance of GatewayRequest prepared for proxy.
     */
    public static GatewayRequest createGatewayRequest(ServerWebExchange exchange, Route route, DataBuffer requestBody) {
        ServerHttpRequest serverHttpRequest= exchange.getRequest();
        HttpHeaders headers = HttpHeaders.writableHttpHeaders(serverHttpRequest.getHeaders());
        MultiValueMap<String, String> queries= serverHttpRequest.getQueryParams();
        Map<String, String> pathParams= ServerWebExchangePropertiesUtils.getUriTemplateVariables(exchange);

        GatewayRequest gatewayRequest= new GatewayRequest();
        gatewayRequest.setHost(route.getHost());
        gatewayRequest.setRoute(route);
        gatewayRequest.setAbsoluteUri(getUpstreamUrl(route, serverHttpRequest));
        gatewayRequest.setHeaders(headers);
        gatewayRequest.setHttpMethod(getUpstreamMethod(route, serverHttpRequest));
        gatewayRequest.setQueries(queries);
        gatewayRequest.setBody(requestBody);
        gatewayRequest.setPathParams(pathParams);

        // Set Upstream Host Headers
        setHostHeaders(gatewayRequest);

        return gatewayRequest;
    }

    private static String getUpstreamUrl(Route route, ServerHttpRequest serverHttpRequest) {

        String url= route.getHost().getUri();
        url += getContextPath(route, serverHttpRequest);
        log.debug("Prepared Upstream URL: {}", url);
        return url;
    }

    private static String getContextPath(Route routeProperties, ServerHttpRequest serverHttpRequest) {
        if(routeProperties.getUpstream().getContextPath() == null) {
            return serverHttpRequest.getURI().getPath();
        }
        return routeProperties.getUpstream().getContextPath();
    }

    private static HttpMethod getUpstreamMethod(Route routeProperties, ServerHttpRequest serverHttpRequest) {
        HttpMethod method= Optional.ofNullable(routeProperties.getUpstream().getMethod()).orElse(serverHttpRequest.getMethod());
        log.debug("Prepared Upstream Method: {}", method);
        return method;
    }

    private static void setHostHeaders(GatewayRequest gatewayRequest) {
        String upstreamHostName= getHostName(gatewayRequest.getHost());
        gatewayRequest.setHeader(HOST_HEADER_NAME, upstreamHostName);
    }

    private static String getHostName(Host host) {
        URI uri= URI.create(host.getUri());
        return uri.getHost();
    }

}
