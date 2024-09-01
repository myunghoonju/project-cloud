package com.app.clienttwo;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CircuitBreakerService {

  private static final String SIMPLE_CIRCUIT_BREAKER = "simpleCircuitBreaker";

  @CircuitBreaker(name = SIMPLE_CIRCUIT_BREAKER, fallbackMethod = "fallback")
  public String process(String param) throws InterruptedException {
    return callServer(param);
  }


  private String fallback(String param, RecordException ex) {
    log.info("RecordException fallback! your request is {} ", param);
    return "RecordException Recovered: " + ex.toString();
  }

  private String fallback(String param, IgnoreException ex) {
    log.info("IgnoreException fallback! your request is {} ", param);
    return "IgnoreException Recovered: " + ex.toString();
  }

  // must be handled
  private String fallback(String param, CallNotPermittedException ex) {
    return "CallNotPermittedException Recovered: ";
  }

  public String callServer(String param) throws InterruptedException {
    if ("a".equals(param)) {
      throw new RecordException("record exception");
    }
    else if ("b".equals(param)) {
      throw new IgnoreException("ignore exception");
    }
    else if ("c".equals(param)) {
      // 3초 이상 걸리는 경우도 실패로 간주
      Thread.sleep(4000);
    }

    return param;
  }
}
