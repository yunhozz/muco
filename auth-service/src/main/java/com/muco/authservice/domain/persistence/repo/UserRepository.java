package com.muco.authservice.domain.persistence.repo;

import com.muco.authservice.domain.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);

    @Query("select u from UserProfile up join up.user u where up.email = :email")
    Optional<User> findWhereEmail(@Param("email") String email);
}