package com.muco.authservice.persistence.repo;

import com.muco.authservice.persistence.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    boolean existsUserProfileByEmail(String email);

    @Query("select up.email from UserProfile up join up.user u where u.id = :userId")
    Optional<String> findEmailByUserId(Long userId);
}