package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.domain.persistence.entity.Musician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicianRepository extends JpaRepository<Musician, Long> {
}