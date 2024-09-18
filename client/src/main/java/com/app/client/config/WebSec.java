package com.app.client.config;

import com.app.client.domain.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSec {

    private final UserService service;

    public WebSec(UserService service) {
        this.service = service;
    }

//    // 인증
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
//    }

    // 권한
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()).csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
//
//    private AuthFilter authFilter(UserService service) throws Exception{
//        AuthFilter authFilter = new AuthFilter(service);
//        authFilter.setAuthenticationManager(authenticationManager());
//
//        return authFilter;
//    }
}
