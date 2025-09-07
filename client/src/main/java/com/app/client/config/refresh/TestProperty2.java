package com.app.client.config.refresh;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RefreshFlag
@Getter @Setter
public class TestProperty2 {

  @Value("${config}")
  private String msg2;

  @Value("${config.string}")
  private String stringVal2;
}
