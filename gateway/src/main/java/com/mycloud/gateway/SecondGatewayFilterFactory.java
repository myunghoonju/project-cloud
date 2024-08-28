package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

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
      ServerWebExchange xxx = exchange.mutate().request(request.mutate().uri(URI.create("http://localhost:8989")).path("/welcome").method(config.getMethod()).build()).build();
      return chain.filter(xxx);
//      return chain.filter(exchange.mutate()
//                                  .request(request.mutate()
//                                                  .method(config.getMethod()).build())
//                                  .build());
    }, Ordered.HIGHEST_PRECEDENCE);
  }
}
