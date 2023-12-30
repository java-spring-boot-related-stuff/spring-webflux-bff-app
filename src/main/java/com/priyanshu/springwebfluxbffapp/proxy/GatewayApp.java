package com.priyanshu.springwebfluxbffapp.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyanshu.springwebfluxbffapp.proxy.condition.GatewayAppEnabledCondition;
import com.priyanshu.springwebfluxbffapp.proxy.config.GatewayAppConfig;
import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayRequest;
import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayResponse;
import com.priyanshu.springwebfluxbffapp.proxy.model.route.Route;
import com.priyanshu.springwebfluxbffapp.proxy.utils.ServerWebExchangeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Conditional(GatewayAppEnabledCondition.class)
public class GatewayApp {

    private static final Logger log= LoggerFactory.getLogger(GatewayApp.class);

    private final GatewayAppConfig gatewayAppConfig;

    private static final String defaultUpstreamConnectionErrorResponse= "{\"status\":false,\"message\":\"Unable to Connect With Upstream\"}";

    public GatewayApp(GatewayAppConfig gatewayAppConfig) {
        this.gatewayAppConfig= gatewayAppConfig;
    }

    public Mono<ResponseEntity<DataBuffer>> handle(ServerWebExchange exchange, DataBuffer requestBody) {
        Route selectedRoute=gatewayAppConfig.selectRoute(exchange);
        GatewayRequest gatewayRequest= ServerWebExchangeUtils.createGatewayRequest(exchange, selectedRoute, requestBody);

        ProxyExchange proxyExchange= new ProxyExchange()
                .gatewayRequest(gatewayRequest)
                .build();

        return proxyExchange.exchange()
                .flatMap(gatewayResponse -> Mono.just(this.mapGatewayResponseToHttpResponse(gatewayResponse)));
    }

    private ResponseEntity<DataBuffer> mapGatewayResponseToHttpResponse(GatewayResponse gatewayResponse) {
        HttpHeaders httpHeaders= HttpHeaders.readOnlyHttpHeaders(gatewayResponse.getHeaders());
        int statusCode= gatewayResponse.getStatusCode();
        DataBuffer responseBody;

        Object body= gatewayResponse.getBody();
        Object errorBody= gatewayResponse.getErrorBody();

        if(statusCode == -1) {
            responseBody= stringToDataBuffer(defaultUpstreamConnectionErrorResponse);
            statusCode= HttpStatus.BAD_GATEWAY.value();
        }
        else {
            responseBody= objectToDataBuffer(body != null ? body : errorBody != null ? errorBody : "");
        }
        return new ResponseEntity<DataBuffer>(responseBody, httpHeaders, HttpStatus.valueOf(statusCode));
    }

    private static DataBuffer stringToDataBuffer(String inputString) {
        // Use DefaultDataBufferFactory to create a DataBuffer
        DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();

        // Convert String to byte array
        byte[] bytes = inputString.getBytes();

        // Create a DataBuffer from the byte array
        return bufferFactory.wrap(bytes);
    }

    private static DataBuffer objectToDataBuffer(Object object) {

        // If Object is already DataBuffer, then no need to convert.
        if(object instanceof DataBuffer dataBuffer) {
            return dataBuffer;
        }

        // If Object is instance of string, then directly get bytes from string and return.
        if(object instanceof String inputString) {
            return stringToDataBuffer(inputString);
        }

        // Use Jackson ObjectMapper to convert object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] objectBytes;
        try {
            objectBytes = objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert provided object to bytes array using jackson", e);
            return objectToDataBuffer("Internal Server Error");
        }

        // Use DefaultDataBufferFactory to create a DataBuffer
        DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        // Create a DataBuffer from the byte array and return
        return bufferFactory.wrap(objectBytes);
    }
}
