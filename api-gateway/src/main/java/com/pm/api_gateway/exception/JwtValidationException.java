package com.pm.api_gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestControllerAdvice // tells spring that this class will handle an exception
public class JwtValidationException {
    // mono object is used by spring to tell the filter chain that it's execution is finished
    // intercept the 500 internal server error from the api-gateway and set it to Unauthorized

    // based on Spring documentation the class passed in the annotation is nested class of the WebClientResponseException
    @ExceptionHandler(WebClientResponseException.Unauthorized.class)
    public Mono<Void> handleUnauthorizedException(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
