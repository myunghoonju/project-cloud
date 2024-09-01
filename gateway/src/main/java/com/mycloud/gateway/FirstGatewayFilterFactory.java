package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

@GateWayFilterFactory
public class FirstGatewayFilterFactory extends AbstractGatewayFilterFactory<FirstGatewayFilterFactory.Config> {

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
          return chin.filter(ex);

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
