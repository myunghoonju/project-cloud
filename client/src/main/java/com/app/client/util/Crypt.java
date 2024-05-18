package com.app.client.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Crypt {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static String encode(String password) {
        return ENCODER.encode(password);
    }
}
