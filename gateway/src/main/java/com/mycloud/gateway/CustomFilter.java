package com.mycloud.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import com.mycloud.gateway.CustomFilter.CustomConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomConfig> {

    public CustomFilter() {
        super(CustomConfig.class);
    }

    @Override
    public GatewayFilter apply(CustomConfig customConfig) {
        return new OrderedGatewayFilter((ex, chin) -> {
            ServerHttpRequest req = ex.getRequest();
            ServerHttpResponse res = ex.getResponse();
            System.err.println("CustomFilter req id: " + req.getId());

            return chin.filter(ex)
                       .then(Mono.fromRunnable(() -> System.err.println("CustomFilter res status" + res.getStatusCode())));
        }, 4);
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class CustomConfig {
        private String name;
        private String value;
    }
}
