package com.mycloud.gateway;

import com.mycloud.gateway.annotation.GateWayFilterFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractChangeRequestUriGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Optional;

// client - 8989
// client-** - 8888

@Slf4j
@GateWayFilterFactory
public class ModifyUrI extends AbstractChangeRequestUriGatewayFilterFactory<ModifyUrI.Config> {

  public ModifyUrI() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return super.apply(config);
  }

  @Override
  protected Optional<URI> determineRequestUri(ServerWebExchange exchange, Config config) {
    String path = exchange.getRequest().getURI().getPath();
    log.info("ModifyUrI determineRequestUri {} ", exchange.getRequest().getURI().getPath());

    if (path.contains("/-")) {
      String s1 = path.replaceFirst("/-one", "");
      String s2 = s1.replaceFirst("/-two", "");
      String s3 = s2.replaceFirst("/-three", "");
      return Optional.of(URI.create("http://localhost:8989" + s3));
    }

    return Optional.of(URI.create("http://localhost:8888" + path));
  }

  @Getter @Setter
  public static class Config {
    private String name;
  }
}
