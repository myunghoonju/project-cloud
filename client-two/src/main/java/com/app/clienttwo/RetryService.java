package com.app.clienttwo;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RetryService {

  private static final String RETRYSERVICE = "retryService";

  @Retry(name = "retryService", fallbackMethod = "fallback")
  public String process(String param) {
    return callAnotherServer(param);
  }

  private String fallback(String param, Exception ex) {
    // retry에 전부 실패해야 fallback이 실행
    log.info("fallback! your request is " + param);
    return "Recovered: " + "hooray";
  }

  private String callAnotherServer(String param) {
    // retry exception은 retry된다.
//    throw new RetryException("retry exception");
    throw new RetryException("callAnotherServer exception");
    // ignore exception은 retry하지 않고 바로 예외가 클라이언트에게 전달된다.
//        throw new IgnoreException("ignore exception");
  }
}
