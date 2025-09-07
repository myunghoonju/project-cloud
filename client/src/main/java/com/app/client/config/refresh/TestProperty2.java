package com.app.client.config.refresh;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Getter
@Component
@RefreshScope
public class TestProperty2 {

  @Value("${config}")
  private String msg2;

  @Value("${config.string}")
  private String stringVal2;
}
