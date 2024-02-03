package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.domain.persistence.entity.Musician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicianRepository extends JpaRepository<Musician, Long> {
    Optional<Musician> findByUserId(Long userId);

    default Musician getMusicianWhenExistsByUserId(Long userId) {
        return findByUserId(userId).orElse(null);
    }
}