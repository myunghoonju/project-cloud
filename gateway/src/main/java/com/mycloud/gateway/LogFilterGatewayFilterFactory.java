package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@GateWayFilterFactory
public class LogFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<LogFilterGatewayFilterFactory.LogConfig> {

    public LogFilterGatewayFilterFactory() {
        super(LogConfig.class);
    }

    @Override
    public GatewayFilter apply(LogConfig globalConfig) {
        return new OrderedGatewayFilter(delegate(globalConfig), 1);
    }

    @Getter
    public static class LogConfig {
        private String msg;
        private boolean preLog;
        private boolean postLog;

        public LogConfig setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public LogConfig setPreLog(boolean preLog) {
            this.preLog = preLog;
            return this;
        }

        public LogConfig setPostLog(boolean postLog) {
            this.postLog = postLog;
            return this;
        }
    }


    private GatewayFilter delegate(LogConfig config) {
        return (ex, chin) -> {
            ServerHttpRequest req = ex.getRequest();
            ServerHttpResponse res = ex.getResponse();
            System.err.println("LogFilter msg: " + config.getMsg());

            if (config.preLog) {
                System.err.println("LogFilter req id: " + req.getId());
            }

            return chin.filter(ex)
                    .then(Mono.fromRunnable(() -> {
                        if (config.postLog) {
                            System.err.println("LogFilter res status" + res.getStatusCode());
                        }
                    }));
        };
    }
}
