package com.mycloud.gateway;

import lombok.Getter;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    public GlobalFilter(ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {
      super(Config.class);
      this.reactiveCircuitBreakerFactory = reactiveCircuitBreakerFactory;
    }

    @Override
    public GatewayFilter apply(Config globalConfig) {
        return (ex, chin) -> {
            ServerHttpRequest req = ex.getRequest();
            ServerHttpResponse res = ex.getResponse();
            System.err.println("GlobalFilter msg: " + globalConfig.getMsg());

            if (globalConfig.preLog) {
                System.err.println("GlobalFilter req id: " + req.getId());
            }
            ReactiveCircuitBreaker aaa = reactiveCircuitBreakerFactory.create("aaa");
            return aaa.run(chin.filter(ex)
                    .then(Mono.fromRunnable(() -> {
                        if (globalConfig.postLog) {
                            System.err.println("GlobalFilter res status" + res.getStatusCode());
                        }
                    })), throwable -> {
                System.err.println("reactiveCircuitBreakerFactory " + throwable.getMessage());
                return Mono.error(throwable);
            });
        };
    }

    @Getter
    public static class Config {
        private String msg;
        private boolean preLog;
        private boolean postLog;

        public Config setMsg(String setMsg) {
            this.msg = setMsg;
            return this;
        }

        public Config setPreLog(boolean preLog) {
            this.preLog = preLog;
            return this;
        }

        public Config setPostLog(boolean postLog) {
            this.postLog = postLog;
            return this;
        }
    }
}
