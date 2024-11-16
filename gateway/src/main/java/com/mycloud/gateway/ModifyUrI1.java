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

@Slf4j
@GateWayFilterFactory
public class ModifyUrI1 extends AbstractChangeRequestUriGatewayFilterFactory<ModifyUrI1.Config> {

  public ModifyUrI1() {
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

    if (path.contains("-one")) {
      String s1 = path.replaceFirst("/-one", "");
      return Optional.of(URI.create("http://localhost:8989" + s1));
    }

    if (path.contains("-two")) {
      String s1 = path.replaceFirst("/-two", "");
      return Optional.of(URI.create("http://localhost:8989" + s1));
    }

    if (path.contains("-three")) {
      if (path.contains("-three-half")) {
        String s1 = path.replaceFirst("/-three-half", "");
        return Optional.of(URI.create("http://localhost:8989" + s1));
      }

      String s1 = path.replaceFirst("/-three", "");
      return Optional.of(URI.create("http://localhost:8989" + s1));
    }

   return Optional.of(URI.create("http://localhost:8888" + path));
  }

  @Getter @Setter
  public static class Config {
    private String name;
  }
}
