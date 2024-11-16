package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractChangeRequestUriGatewayFilterFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@GateWayFilterFactory
public class ModifyUrI2 extends AbstractChangeRequestUriGatewayFilterFactory<ModifyUrI2.Config> {

  public ModifyUrI2() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return super.apply(config);
  }

  @Override
  protected Optional<URI> determineRequestUri(ServerWebExchange exchange, Config config) {
    String path = exchange.getRequest().getURI().getPath();
    log.info("ModifyUrI2 determineRequestUri {} ", exchange.getRequest().getURI().getPath());

    MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
    List<String> type = queryParams.get("type");
    if (type != null) {
      return Optional.of(URI.create("http://localhost:8989" + path));
    }

    return Optional.of(URI.create("http://localhost:8888" + path));
  }

  @Getter @Setter
  public static class Config {
    private String name;
  }
}
