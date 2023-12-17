package com.muco.authservice.persistence.repo;

import com.muco.authservice.persistence.query.UserInfoQueryDTO;

import java.util.Optional;

public interface UserProfileCustomRepository {

    Optional<UserInfoQueryDTO> findUserInfoById(Long id);
}