package com.app.client.domain;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String password;


    public ClientUser toEntity() {
        return ClientUser.builder().name(name).email(email).password(password).build();
    }

    public static UserVO toRes(ClientUser MUser) {
        return new UserVO(MUser.getName(), MUser.getEmail(), MUser.getPassword());
    }
}
