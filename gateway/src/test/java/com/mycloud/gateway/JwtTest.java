package com.mycloud.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTest {

    @Test
    void token() {
        SecretKey secretKey =  Keys.hmacShaKeyFor("my_over_forty_length_of_letters_for_secret_message".getBytes(StandardCharsets.UTF_8));
        SecretKey keyForDecrypt =  Keys.hmacShaKeyFor("my_over_forty_length_of_letters_for_secret_message".getBytes(StandardCharsets.UTF_8));

        String token  = Jwts.builder()
                                    .setExpiration(new Date(System.currentTimeMillis() + 60000L))
                                    .setSubject("me")
                                    .signWith(secretKey)
                                    .compact();

        Claims body = Jwts.parserBuilder()
                          .setSigningKey(keyForDecrypt)
                          .build()
                          .parseClaimsJws(token)
                          .getBody();

        String o = (String) body.get("sub");

        assertThat("me").isEqualTo(o);
    }
}
