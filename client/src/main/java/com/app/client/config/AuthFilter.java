package com.app.client.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.client.domain.UserService;
import com.app.client.domain.UserVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService service;

    public AuthFilter(UserService service) {
        this.service = service;
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginVO loginVO = mapper.readValue(request.getInputStream(), LoginVO.class);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginVO.getName(), loginVO.getPassword(), new ArrayList<>());

            return getAuthenticationManager().authenticate(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("fail to attemptAuthentication - ", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        User principal = (User) authResult.getPrincipal();
        UserVO userVO = service.userDetails(principal.getUsername());
        SecretKey secretKey =  Keys.hmacShaKeyFor("my_over_forty_length_of_letters_for_secret_message".getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                           .setSubject(userVO.getName())
                           .setExpiration(new Date(System.currentTimeMillis() + 3600000L))
                           .signWith(secretKey)
                           .compact();

        response.addHeader("token", token);
        response.addHeader("userName", userVO.getName());
    }
}
