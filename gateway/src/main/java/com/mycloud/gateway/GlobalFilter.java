package com.mycloud.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import com.mycloud.gateway.GlobalFilter.GlobalConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalConfig> {

    public GlobalFilter() {
        super(GlobalConfig.class);
    }

    @Override
    public GatewayFilter apply(GlobalConfig globalConfig) {
        return (ex, chin) -> {
            ServerHttpRequest req = ex.getRequest();
            ServerHttpResponse res = ex.getResponse();
            System.err.println("GlobalFilter msg: " + globalConfig.getMsg());

            if (globalConfig.preLog) {
                System.err.println("GlobalFilter req id: " + req.getId());
            }

            return chin.filter(ex)
                       .then(Mono.fromRunnable(() -> {
                           if (globalConfig.postLog) {
                               System.err.println("GlobalFilter res status" + res.getStatusCode());
                           }
                       }));
        };
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class GlobalConfig {
        private String msg;
        private boolean preLog;
        private boolean postLog;
    }
}
