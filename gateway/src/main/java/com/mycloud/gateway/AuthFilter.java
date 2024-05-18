package com.mycloud.gateway;

import java.nio.charset.StandardCharsets;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    Environment env;

    public AuthFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    @Override
    public GatewayFilter apply(Config config) {
        String secret = env.getProperty("token.secret");
        return new OrderedGatewayFilter(((ex, chin) -> {
            log.error("second filter - AuthFilter & secret {}", secret);
            ServerHttpRequest req = ex.getRequest();
            if (!req.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(ex, "no auth", HttpStatus.UNAUTHORIZED);
            }

            HttpHeaders headers = req.getHeaders();
            String header = headers.get(HttpHeaders.AUTHORIZATION).get(0);
            String token = header.replace("Bearer ", "");
            if (invalidated(token)) {
                return onError(ex, "empty token", HttpStatus.UNAUTHORIZED);
            }

            return chin.filter(ex);
        }),2) ;
    }

    private Mono<Void> onError(ServerWebExchange ex,
                               String errMsg,
                               HttpStatus httpStatus) {
        ServerHttpResponse response = ex.getResponse();
        response.setStatusCode(httpStatus);

        log.error("token error msg {}", errMsg);

        return response.setComplete();
    }

    private boolean invalidated(String token) {
        // String secret = env.getProperty("token.secret");
        String secret = "my_over_forty_length_of_letters_for_secret_message";
        if (StringUtils.hasText(secret)) {
            String o = Jwts.parserBuilder()
                           .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                           .build()
                           .parseClaimsJws(token)
                           .getBody()
                           .getSubject();

            log.error("token o {}", o);

            return !StringUtils.hasText(o);
        }

        return false;
    }

    public static class Config {}
}
