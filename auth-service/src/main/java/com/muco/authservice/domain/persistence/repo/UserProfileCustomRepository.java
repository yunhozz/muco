package com.muco.authservice.domain.persistence.repo;

import com.muco.authservice.global.dto.query.UserInfoQueryDTO;

import java.util.List;
import java.util.Optional;

public interface UserProfileCustomRepository {

    Optional<UserInfoQueryDTO> findUserInfoById(Long id);
    List<UserInfoQueryDTO> findUserInfoListByIds(List<Long> ids);
}