package com.muco.authservice.persistence.repo;

import com.muco.authservice.persistence.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("select up from UserProfile up join fetch up.user u where up.email = :email")
    Optional<UserProfile> findWithUserByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}