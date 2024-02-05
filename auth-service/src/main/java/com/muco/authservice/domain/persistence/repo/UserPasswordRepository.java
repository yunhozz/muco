package com.muco.authservice.domain.persistence.repo;

import com.muco.authservice.domain.persistence.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {

    @Query("select up from UserPassword up join up.user u where u.id = :userId")
    Optional<UserPassword> findUserPasswordByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update UserPassword up set up.retryCount = up.retryCount + 1 where up.id = :id")
    void addRetryCountById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("update UserPassword up set up.retryCount = 0 where up.id = :id")
    void resetRetryCountById(@Param("id") Long id);
}