package com.muco.authservice.persistence.repo;

import com.muco.authservice.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);

    @Query("select u from UserProfile up join up.user u where up.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);
}