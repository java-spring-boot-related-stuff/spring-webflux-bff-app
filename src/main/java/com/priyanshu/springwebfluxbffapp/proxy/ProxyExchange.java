package com.priyanshu.springwebfluxbffapp.proxy;

import com.priyanshu.springwebfluxbffapp.proxy.model.GatewayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

public class ProxyExchange<T> {

    private static final Logger log= LoggerFactory.getLogger(ProxyExchange.class);

    private GatewayRequest gatewayRequest;

    private WebClient webClient;


    public ProxyExchange<T> gatewayRequest(GatewayRequest gatewayRequest) {
        this.gatewayRequest= gatewayRequest;
        return this;
    }

    public ProxyExchange<T> build() {
        if(null== this.webClient) {
            this.webClient= webClient();
        }

        return this;
    }


    public Mono<ResponseEntity<T>> exchange() {

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

        return result.onStatus(HttpStatusCode::isError, t -> Mono.empty())
                .toEntity(ParameterizedTypeReference.forType(String.class));
    }

    private static ExchangeFilterFunction handleNon200Status() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode() != HttpStatus.OK) {
                // Handle non-200 status codes
                // You can log, transform, or propagate the error downstream as needed
                System.err.println("Non-200 status code: " + clientResponse.statusCode());

                // Returning the original response ensures that the error is propagated downstream
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.debug(errorBody);
                            return Mono.just(clientResponse);
                        });
            }

            // Continue with the original response for 200 status codes
            return Mono.just(clientResponse);
        });
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
