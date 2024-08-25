package com.mycloud.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import com.mycloud.gateway.LogFilter.LogConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Component
public class LogFilter extends AbstractGatewayFilterFactory<LogConfig> {

    public LogFilter() {
        super(LogConfig.class);
    }

    @Override
    public GatewayFilter apply(LogConfig globalConfig) {
        return new OrderedGatewayFilter(delegate(globalConfig), 3);
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class LogConfig {
        private String msg;
        private boolean preLog;
        private boolean postLog;
    }


    private GatewayFilter delegate(LogConfig globalConfig) {
        return (ex, chin) -> {
            ServerHttpRequest req = ex.getRequest();
            ServerHttpResponse res = ex.getResponse();
            System.err.println("LogFilter msg: " + globalConfig.getMsg());

            if (globalConfig.preLog) {
                System.err.println("LogFilter req id: " + req.getId());
            }

            return chin.filter(ex)
                    .then(Mono.fromRunnable(() -> {
                        if (globalConfig.postLog) {
                            System.err.println("LogFilter res status" + res.getStatusCode());
                        }
                    }));
        };
    }
}
