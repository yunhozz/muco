package com.muco.authservice.persistence.repo;

import com.muco.authservice.persistence.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {
}