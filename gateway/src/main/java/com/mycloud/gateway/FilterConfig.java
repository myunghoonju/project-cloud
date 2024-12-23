package com.mycloud.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator firstRouteLocator(RouteLocatorBuilder builder,
                                          ModifyUrI1 modifyUrI,
                                          ModifyUrI2 modifyUrI2,
                                          FirstGatewayFilterFactory custom) {
        return builder.routes()
                .route(it -> it.path("/client/**")
                                             .filters(paramCondition(modifyUrI2, custom))
                                             .uri("http://localhost:8989"))
                .route("user-application", it -> it.path("/client-one/**",
                                                                          "/client-two/**",
                                                                          "/client-three/**",
                                                                          "/client-three-half/**")
                        .filters(pathConfidion(modifyUrI, custom))
                        .uri("http://localhost:8989"))
                .build();

    }

    private Function<GatewayFilterSpec, UriSpec> pathConfidion(ModifyUrI1 modifyUrI,
                                                               FirstGatewayFilterFactory custom) {
        return config -> config.rewritePath("/client(?<segment>.*)", "/${segment}")
                                                .filters(modifyUrI.apply(g -> g.setName("modify uri")),
                                                         custom.apply(g -> g.setMsg("custom filter")));
    }

    private Function<GatewayFilterSpec, UriSpec> paramCondition(ModifyUrI2 modifyUrI,
                                                                FirstGatewayFilterFactory custom) {
        return config -> config.rewritePath("/client(?<segment>.*)", "${segment}")
                                                .filters(modifyUrI.apply(g -> g.setName("modify uri")),
                                                         custom.apply(g -> g.setMsg("custom filter")));
    }
}
