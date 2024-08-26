package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

@GateWayFilterFactory
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
                                                                }})),
                               throwable -> {
                                               addOriginalRequestUrl(ex,req.getURI());

                                               ServerWebExchange modified = ex.mutate().request(ex.getRequest().mutate().uri(URI.create("https://www.naver.com")).path("/").build()).build();
                                               modified.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, "https://www.naver.com");
                                               System.err.println("reactiveCircuitBreakerFactory " + throwable.getMessage());
                                               return chin.filter(modified);});
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
