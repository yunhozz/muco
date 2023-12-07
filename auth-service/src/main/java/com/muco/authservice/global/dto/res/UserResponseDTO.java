package com.muco.authservice.global.dto.res;

import com.muco.authservice.global.enums.LoginType;
import com.muco.authservice.global.enums.Role;
import com.muco.authservice.persistence.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private LoginType loginType;
    private String role;

    public UserResponseDTO(User user) {
        id = user.getId();
        loginType = user.getLoginType();
        role = user.getRoles().stream()
                .map(Role::getInfo)
                .collect(Collectors.joining(", "));
    }
}