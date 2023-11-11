package com.muco.authservice.persistence.repo;

import com.muco.authservice.persistence.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    boolean existsUserProfileByEmail(String email);
}