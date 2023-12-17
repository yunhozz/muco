package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.persistence.entity.Musician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicianRepository extends JpaRepository<Musician, Long> {
}