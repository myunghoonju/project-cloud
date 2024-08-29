package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@GateWayFilterFactory
public class FirstGatewayFilterFactory extends AbstractGatewayFilterFactory<FirstGatewayFilterFactory.Config> {

    @Autowired
    ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory;

    public FirstGatewayFilterFactory() {
      super(Config.class);

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

          ReactiveCircuitBreaker test = reactiveResilience4JCircuitBreakerFactory.create("test");

          return test.run(chin.filter(ex), throwable -> {
            System.err.println("GlobalFilter res id: " + req.getId());
            res.setStatusCode(HttpStatusCode.valueOf(200));
            ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(res);
            DataBuffer dataBuffer = decorator.bufferFactory().wrap("newResponseBody".getBytes(StandardCharsets.UTF_8));
            decorator.writeWith(Mono.just(dataBuffer)).subscribe(System.out::println);
            ex.mutate().response(decorator).build();
            return chin.filter(ex);
          });

//            ReactiveCircuitBreaker aaa = reactiveCircuitBreakerFactory.create("aaa");
//            return aaa.run(chin.filter(ex)
//                               .then(Mono.fromRunnable(() -> {
//                                                                if (globalConfig.postLog) {
//                                                                    System.err.println("GlobalFilter res status" + res.getStatusCode());
//                                                                }})),
//                               throwable -> {
//                                               ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(res);
//                                               decorator.setStatusCode(HttpStatus.OK);
//                                               DataBuffer dataBuffer = decorator.bufferFactory().wrap("newResponseBody".getBytes(StandardCharsets.UTF_8));
//                                               decorator.writeWith(Mono.just(dataBuffer)).subscribe();
//                                               System.err.println("reactiveCircuitBreakerFactory ");
//                                 return chin.filter(ex.mutate().response(decorator).build());});
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
