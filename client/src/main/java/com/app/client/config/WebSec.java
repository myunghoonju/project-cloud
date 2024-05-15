package com.app.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.app.client.domain.UserService;

@Configuration
@EnableWebSecurity
public class WebSec extends WebSecurityConfigurerAdapter {

    private final UserService service;
    private final Crypt crypt;

    public WebSec(UserService service, Crypt crypt) {
        this.service = service;
        this.crypt = crypt;
    }

    // 인증
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
    }

    // 권한
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();

        http.authorizeRequests().antMatchers("/users/**", "/h2-console").permitAll();
        http.addFilter(authFilter(crypt, service));
    }

    private AuthFilter authFilter(Crypt crypt, UserService service) throws Exception{
        AuthFilter authFilter = new AuthFilter(crypt, service);
        authFilter.setAuthenticationManager(authenticationManager());

        return authFilter;
    }
}
