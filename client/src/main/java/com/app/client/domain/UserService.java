package com.app.client.domain;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.client.config.Crypt;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final Crypt crypt;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        ClientUser clientUser = repository.findByName(s);
        if (clientUser == null) {
            throw new IllegalArgumentException("unknown user - " + s);
        }
        return new User(clientUser.getName(), clientUser.getPassword(), true, true, true, true, new ArrayList<>());
    }

    public void save(UserVO userVO) {
        String encoded = crypt.encode(userVO.getPassword());
        userVO.setPassword(encoded);

        repository.save(userVO.toEntity());
    }

    public UserVO user(Long id) {
        return UserVO.toRes(repository.findById(id).get());
    }

    public UserVO userDetails(String username) {
        return UserVO.toRes(repository.findByName(username));
    }
}
