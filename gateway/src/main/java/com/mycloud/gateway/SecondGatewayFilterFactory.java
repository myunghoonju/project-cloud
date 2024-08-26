package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;

@GateWayFilterFactory
public class SecondGatewayFilterFactory extends AbstractGatewayFilterFactory<SecondGatewayFilterFactory.Config> {

  public SecondGatewayFilterFactory() {
    super(Config.class);
  }

  @Getter
  public static class Config {

    private HttpMethod method;

    public Config setMethod(HttpMethod method) {
      this.method = method;
      return this;
    }
  }

  @Override
  public GatewayFilter apply(Config config) {
    return new OrderedGatewayFilter((exchange, chain) -> {
      System.err.println("SecondGatewayFilterFactory");
      ServerHttpRequest request = exchange.getRequest();
      return chain.filter(exchange.mutate()
                                  .request(request.mutate()
                                                  .method(config.getMethod()).build())
                                  .build());
    }, Ordered.HIGHEST_PRECEDENCE);
  }
}
