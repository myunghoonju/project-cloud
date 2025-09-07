package com.app.client.config.refresh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PropertyUpdater {

  private final RefreshScope scope;
  private final ContextRefresher contextRefresher;
  private final ConfigurableEnvironment environment;
  private final ApplicationContext applicationContext;

  public PropertyUpdater(RefreshScope scope,
                         @Qualifier("configDataContextRefresher")
                         ContextRefresher contextRefresher,
                         ConfigurableEnvironment environment,
                         ApplicationContext applicationContext) {
    this.scope = scope;
    this.contextRefresher = contextRefresher;
    this.environment = environment;
    this.applicationContext = applicationContext;
  }

  public void updateProperties() {
//    contextRefresher.refresh();// fetch and recreate
//    mentalModifyConfig();

    Map<String, Object> flags = applicationContext.getBeansWithAnnotation(RefreshFlag.class);
    contextRefresher.refreshEnvironment(); // fetch
    for (Map.Entry<String, Object> entry : flags.entrySet()) {
      scope.refresh(entry.getKey());
//      if ("testProperty2".equals(entry.getKey())) {
//        TestProperty2 value = (TestProperty2) entry.getValue();
//        value.setMsg2("modified");
//        value.setStringVal2("modified");
//
//        entry.setValue(value);
//      }
    }
  }

  private void mentalModifyConfig() {
    MutablePropertySources properties = environment.getPropertySources();
    boolean contains = properties.contains("configserver:user-application-dev");
    if (contains) {
      MapPropertySource source = (MapPropertySource) properties.get("configserver:user-application-dev");
      source.getSource().put("config.string", "modified");
    }
  }
}
