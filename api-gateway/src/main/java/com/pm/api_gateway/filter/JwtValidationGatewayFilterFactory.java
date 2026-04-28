package com.pm.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component // manage as Spring Bean
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    
    private final WebClient webClient;

    // take base url of auth service and assign it to authServiceUrl
    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
        @Value("${auth.service.url}") String authServiceUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    // filter class to intercept http request and do processing on it, let it through or reject it, i.e. validating token
    @Override
    public GatewayFilter apply(Object config) {
    // apply filter to all requests - business logic goes here
    // exchange is the request with all properties that is passed to us
    // chain is just a filter
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            if(token == null ||  !token.startsWith("Bearer")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                // returns early from the filter and this reponse is complete and return it to the calling client
                return exchange.getResponse().setComplete();
            }

            // use the web client - make a get request
            // to this uri
            // passing in this token as part of the authorization header
            // then retrieve the response
            // no body in the response
            // if all goes well -> then method is called and let the request pass through
            return webClient.get()
            .uri("/validate")
            .header("Authorization", token)
            .retrieve()
            .toBodilessEntity()
            .then(chain.filter(exchange));
        };
    }
}