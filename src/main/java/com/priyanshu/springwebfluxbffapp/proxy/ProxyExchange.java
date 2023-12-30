package com.priyanshu.springwebfluxbffapp.proxy;

import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayRequest;
import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

public class ProxyExchange {

    private static final Logger log= LoggerFactory.getLogger(ProxyExchange.class);

    private GatewayRequest gatewayRequest;

    private WebClient webClient;

    private int retry= 1;
    
    private Class<?> responseType= DataBuffer.class;


    public ProxyExchange gatewayRequest(GatewayRequest gatewayRequest) {
        this.gatewayRequest= gatewayRequest;
        return this;
    }

    public ProxyExchange build() {
        if(null== this.webClient) {
            this.webClient= webClient();
        }

        return this;
    }

    /**
     * Sets the retry property on ProxyExchange
     * @param retry Retry Value From 0 to 5 Range.
     *  If provided retry < 0, then 0 is set. If it is provided > 5 then 5 is set.
     * @return ProxyExchangeObject itself to implement Builder Pattern.
     */
    public ProxyExchange retry(short retry) {
        this.retry= Math.min(Math.max(retry, 0), 5);
        return this;
    }

    public ProxyExchange responseType(Class<?> responseType) {
        this.responseType= responseType;
        return this;
    }


    public Mono<GatewayResponse> exchange() {

        URI finalUri = buildUri();

        WebClient.RequestBodySpec builder = webClient.method(gatewayRequest.getHttpMethod())
                .uri(finalUri)
                .headers(httpHeaders -> httpHeaders.addAll(gatewayRequest.getHeaders()));

        WebClient.ResponseSpec result;
        if(gatewayRequest.getBody() == null) {
            result= builder.retrieve();
        }
        else {
            result = builder.body(BodyInserters.fromValue(gatewayRequest.getBody())).retrieve();
        }
        return result
                .toEntity(ParameterizedTypeReference.forType(responseType))
                .retryWhen(Retry.backoff(this.retry, Duration.ofSeconds(1)).jitter(0.75))
                .flatMap(this::mapToGatewayResponse)
                .onErrorResume(this::handleError);
    }

    private Mono<GatewayResponse> mapToGatewayResponse(ResponseEntity<Object> entity) {
        GatewayResponse response = new GatewayResponse<>();
        response.setStatusCode(entity.getStatusCode().value());
        response.setHeaders(entity.getHeaders());
        response.setBody(entity.getBody());
        return Mono.just(response);
    }

    private Mono<GatewayResponse<String>> handleError(Throwable throwable) {

        if (throwable instanceof WebClientResponseException ex) {
            return handleWebClientResponseException(ex);
        }
        else if(throwable.getCause() != null && throwable.getCause() instanceof WebClientResponseException ex) {
            return handleWebClientResponseException(ex);
        }
        else if(throwable instanceof WebClientRequestException ex) {
            return handleWebClientRequestException(ex);
        }
        else if(throwable.getCause() != null && throwable.getCause() instanceof WebClientRequestException ex) {
            return handleWebClientRequestException(ex);
        }
        else {
            return handleUnknownException(throwable);
        }

    }

    private Mono<GatewayResponse<String>> handleWebClientRequestException(WebClientRequestException ex) {

        log.error("Unable To Send Request to {}, Exception: {}", ex.getUri(), ex.getMessage(), ex);

        GatewayResponse<String> response = new GatewayResponse<>();
        response.setStatusCode(-1);
        response.setHeaders(new HttpHeaders());
        response.setErrorBody("Unable to Connect With Upstream Server");
        return Mono.just(response);
    }

    private Mono<GatewayResponse<String>> handleWebClientResponseException(WebClientResponseException ex) {

        GatewayResponse<String> response = new GatewayResponse<>();
        response.setStatusCode(ex.getStatusCode().value());
        response.setHeaders(ex.getHeaders());
        response.setErrorBody(ex.getResponseBodyAsString());

        log.warn("Http Status: {} Received From Upstream With Body: {} Exception: ", ex.getStatusCode(),response.getErrorBody(), ex);
        return Mono.just(response);
    }

    private Mono<GatewayResponse<String>> handleUnknownException(Throwable ex) {

        log.error("Unhandled Exception Occurred During Webclient Call", ex);

        GatewayResponse<String> response = new GatewayResponse<>();
        response.setStatusCode(-1); // Custom status code for connection errors
        response.setHeaders(new HttpHeaders());
        response.setErrorBody("Unable to Connect With Upstream Server");
        return Mono.just(response);
    }



    private URI buildUri() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(gatewayRequest.getAbsoluteUri());
        gatewayRequest.getQueries().forEach((key, values) -> values.forEach(value -> builder.queryParam(key, value)));
        URI finalUri=  builder.build().toUri();
        log.debug("Final Upstream URI: {}", finalUri);
        return finalUri;
    }

    private WebClient webClient() {
        final int size = 50 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }


}
