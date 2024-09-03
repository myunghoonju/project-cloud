package com.mycloud.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class ResponseBodyUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static Mono<Object> reWrite(ServerWebExchange exchange, Object o) {
    String rawPath = exchange.getRequest().getURI().getRawPath();
    if (rawPath.equals("/welcome2/aaa")) {
      try {
        List list1 = objectMapper.convertValue(o, List.class);
        WelcomeDto welcomeDto = objectMapper.convertValue(list1.get(0), WelcomeDto.class);

        System.out.println("--->" + welcomeDto.getUsername() + "--->" + welcomeDto.getPassword());

        welcomeDto.setUsername("MODIFIED USERNAME");

        return Mono.just(welcomeDto);
      } catch (Exception e) {
        return Mono.just(o);
      }
    }

    return Mono.just(o);
  }

  public static Mono<List> reWritea(List list) {
    Object o = list.get(0);
    WelcomeDto welcomeDto = objectMapper.convertValue(o, WelcomeDto.class);
    System.out.println("--->" + welcomeDto.getUsername() + "--->" + welcomeDto.getPassword());
    welcomeDto.setUsername("MODIFIED USERNAME");
    return Mono.just(List.of(welcomeDto));
  }
}
