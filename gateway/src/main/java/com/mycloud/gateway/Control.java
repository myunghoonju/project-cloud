package com.mycloud.gateway;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

@RestController
public class Control {

  @GetMapping("/fallback")
  public void fallback(ServerWebExchange exchange) {
    Map<String, Object> attributes = exchange.getAttributes();
    Throwable t = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
    System.err.println("fallback" + t.getMessage());
  }
}
