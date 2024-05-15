package com.app.client.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Crypt {

    private BCryptPasswordEncoder encoder;

    public Crypt() {
        this.encoder = new BCryptPasswordEncoder();
    }

    public String encode(String password) {
        return encoder.encode(password);
    }

    public String decode() {
        return null;
    }
}
