package com.app.client.config.refresh;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@RefreshFlag
@Configuration
public class TestProperty {

  @Value("${config}")
  private String msg;

  @Value("${config.string}")
  private String stringVal;
}
