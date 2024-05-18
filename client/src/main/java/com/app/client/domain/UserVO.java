package com.app.client.domain;

import javax.validation.constraints.NotBlank;

import com.app.client.util.Crypt;

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

    public String password() {
        return Crypt.encode(this.password);
    }

    public ClientUser toEntity() {
        return ClientUser.builder()
                         .name(getName())
                         .email(getEmail())
                         .password(password())
                         .build();
    }

    public static UserVO toRes(ClientUser clientUser) {
        return new UserVO(clientUser.getName(),
                          clientUser.getEmail(),
                          clientUser.getPassword());
    }

    public UserVO toRes() {
        return new UserVO(getName(), getEmail(), password());
    }
}
