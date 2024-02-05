package com.muco.authservice.domain.persistence.repo;

import com.muco.authservice.domain.persistence.query.UserInfoQueryDTO;

import java.util.Optional;

public interface UserProfileCustomRepository {

    Optional<UserInfoQueryDTO> findUserInfoById(Long id);
}