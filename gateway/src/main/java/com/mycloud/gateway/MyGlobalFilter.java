package com.mycloud.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

  private final ModifyRequestBodyGatewayFilterFactory mRBGf;

  public MyGlobalFilter(ModifyRequestBodyGatewayFilterFactory mRBGf) {
    this.mRBGf = mRBGf;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return mRBGf.apply(config(exchange)).filter(exchange, chain);
  }

  private ModifyRequestBodyGatewayFilterFactory.Config config(ServerWebExchange exchange) {
    ModifyRequestBodyGatewayFilterFactory.Config config = new ModifyRequestBodyGatewayFilterFactory.Config();
    config.setInClass(Object.class)
          .setOutClass(Object.class)
          .setRewriteFunction((ex, o) -> {
                                            if (o != null) {
                                              log.info("o {}", o);
                                              return Mono.just(o);
                                            }
                                            log.info("empty body");
                                            return Mono.empty();
                                          });
    return config;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
